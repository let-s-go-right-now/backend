package com.lets.go.right.now.domain.expense.service;

import com.lets.go.right.now.domain.expense.dto.CategoryExpenseRes;
import com.lets.go.right.now.domain.expense.dto.DailyExpenseRes;
import com.lets.go.right.now.domain.expense.dto.ExpenseCreateReq;
import com.lets.go.right.now.domain.expense.dto.ExpensePreviewRes;
import com.lets.go.right.now.domain.expense.dto.ExpenseViewRes;
import com.lets.go.right.now.domain.expense.dto.MemberCategoryExpenseRes;
import com.lets.go.right.now.domain.expense.entity.ExcludedMember;
import com.lets.go.right.now.domain.expense.entity.Expense;
import com.lets.go.right.now.domain.expense.dto.MemberTotalExpenseRes;
import com.lets.go.right.now.domain.expense.dto.TravelTotalExpense;
import com.lets.go.right.now.domain.expense.entity.enums.Category;
import com.lets.go.right.now.domain.settlement.entity.PersonalSpending;
import com.lets.go.right.now.domain.expense.entity.TripImage;
import com.lets.go.right.now.domain.expense.repository.ExcludedMemberRepository;
import com.lets.go.right.now.domain.expense.repository.ExpenseRepository;
import com.lets.go.right.now.domain.settlement.repository.PersonalSpendingRepository;
import com.lets.go.right.now.domain.expense.repository.TripImageRepository;
import com.lets.go.right.now.domain.member.entity.Member;
import com.lets.go.right.now.domain.member.repository.MemberRepository;
import com.lets.go.right.now.domain.trip.entity.Trip;
import com.lets.go.right.now.domain.trip.entity.TripMember;
import com.lets.go.right.now.domain.tripMember.repository.TripMemberRepository;
import com.lets.go.right.now.domain.trip.repository.TripRepository;
import com.lets.go.right.now.global.enums.statuscode.ErrorStatus;
import com.lets.go.right.now.global.exception.GeneralException;
import com.lets.go.right.now.global.response.ApiResponse;
import com.lets.go.right.now.global.s3.service.S3Service;
import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ExpenseServiceImpl implements ExpenseService{
    private final ExpenseRepository expenseRepository;
    private final TripRepository tripRepository;
    private final TripMemberRepository tripMemberRepository;
    private final MemberRepository memberRepository;
    private final TripImageRepository tripImageRepository;
    private final PersonalSpendingRepository personalSpendingRepository;
    private final ExcludedMemberRepository excludedMemberRepository;
    private final S3Service s3Service;

    /**
     * 여행 지출 기록 생성
     */
    @Override
    @Transactional
    public ResponseEntity<?> createExpense(
            Long tripId, ExpenseCreateReq expenseCreateReq, List<MultipartFile> images) throws IOException {
        // 1. 여행 존재 여부 확인
        Trip trip = tripRepository.getTripById(tripId);
        // 2. 결제자 정보 확인
        Member payer = memberRepository.getMemberByEmail(expenseCreateReq.payerEmail());

        // 3. 이미지 업로드
        List<TripImage> tripImages = uploadExpenseImages(images);

        saveExpenseWithTransaction(trip, expenseCreateReq, payer, tripImages);

        return ResponseEntity.ok(ApiResponse.onSuccess("지출 기록이 생성 되었습니다."));
    }

    /**
     * 지출 기록 삭제
     */
    @Override
    @Transactional
    public ResponseEntity<?> deleteExpense(Long expenseId) throws IOException {
        // 1. 지출 기록 존재 여부 확인
        Expense expense = expenseRepository.getExpenseById(expenseId);

        // 2. 지출에 연관된 S3 이미지 삭제
        List<TripImage> tripImages = tripImageRepository.findAllByExpense(expense);
        deleteS3Images(tripImages);

        expenseRepository.delete(expense); // 지출 기록 삭제

        return ResponseEntity.ok(ApiResponse.onSuccess("지출 기록이 삭제 되었습니다."));
    }

    /**
     * 지출 정보 수정
     */
    @Override
    @Transactional
    public ResponseEntity<?> editExpense(
            Long expenseId, ExpenseCreateReq expenseCreateReq, List<MultipartFile> images)
            throws IOException {
        // 1. 지출 조회
        Expense expense = expenseRepository.getExpenseById(expenseId);
        // 2. 결제자 정보 확인
        Member payer = memberRepository.getMemberByEmail(expenseCreateReq.payerEmail());
        // 3. 정보 수정
        expense.editExpense(expenseCreateReq, payer);
        // 4. 기존 이미지 삭제(S3 삭제, 엔티티 삭제), 새로운 이미지 업로드
        List<TripImage> lastTripImages = expense.getTripImages();
        deleteS3Images(lastTripImages);
        tripImageRepository.deleteByExpenseId(expense.getId()); // 엔티티 삭제
        List<TripImage> tripImages = uploadExpenseImages(images); // 새로운 이미지 업로드
        // 4.1. 새로운 이미지 엔티티 저장 및 연관 관계 설정
        for (TripImage tripImage : tripImages) {
            tripImage.changeExpense(expense);
            tripImageRepository.save(tripImage);
        }
        // 4. 기존 지출 제외 멤버 정보 제거
        excludedMemberRepository.deleteByExpenseId(expense.getId());
        // 5. 기존 정산 결과 삭제
        personalSpendingRepository.deleteByExpenseId(expense.getId());
        // 6. 지출 제외 멤버 정보 저장
        List<Member> excludedMembers = saveExcludedMember(expenseCreateReq.excludedMember(), expense);
        // 7. 정산 결과에 반영
        saveMemberSpending(expense.getTrip(), expense, payer, excludedMembers);
        return ResponseEntity.ok(ApiResponse.onSuccess("지출 내역이 수정 되었습니다."));
    }

    /**
     * 지출 기록 보기
     */
    @Override
    public ResponseEntity<?> getExpenseInfo(Long expenseId){
        // 1. 지출 조회
        Expense expense = expenseRepository.getExpenseById(expenseId);
        // 2. 지출 연관 이미지 조회
        List<TripImage> tripImages = tripImageRepository.findAllByExpense(expense);
        ArrayList<String> expenseImageUrls = new ArrayList<>();
        for (TripImage tripImage : tripImages) {
            expenseImageUrls.add(tripImage.getImageUrl());
        }
        // 3. 지출에 참여중인 회원 정보 조회
        // 정산 결과 돈을 보내야 하는 사람들이 정산에 포함된 사람
        List<PersonalSpending> personalSpendingList = personalSpendingRepository.findByExpense(expense);
        List<Member> expenseParticipants = personalSpendingList.stream().map(PersonalSpending::getSender).toList();

        // 4. 반환 DTO 생성 및 반환
        ExpenseViewRes resultDto = ExpenseViewRes.of(expense, expenseImageUrls, expense.getPayer(),
                expenseParticipants);

        return ResponseEntity.ok(ApiResponse.onSuccess(resultDto));
    }

    /**
     * 내가 포함된 지출 보기
     */
    @Override
    public ResponseEntity<?> getMyExpenses(Long tripId, String email, int page, int size) {
        Member member = memberRepository.getMemberByEmail(email);
        // 페이지 쿼리 설정, 정렬 기준 설정
        PageRequest pageRequest = PageRequest.of(page, size);
        Pageable descSortPageable = getDescSortPageable(pageRequest);
        // 1. 연관된 지출 조회 - 정산 결과 조회
        // 1.1. 여행과 연관되고, 회원이 sender로 포함되었으며, expense가 null이 아닌 지출 조회
        Page<PersonalSpending> myPersonaSpending = personalSpendingRepository
                .findMyPersonalSpending(tripId, member.getId(), descSortPageable);

        // 2. 반환 DTO 생성
        ArrayList<ExpensePreviewRes> resultDtoArray = new ArrayList<>();
        for (PersonalSpending personalSpending : myPersonaSpending.getContent()) {
            resultDtoArray.add(ExpensePreviewRes.of(personalSpending.getExpense()));
        }
        return ResponseEntity.ok(ApiResponse.onSuccess(resultDtoArray));
    }

    /**
     * 여행 총 지출 확인 - 여행 회원별 총 지출액 확인
     */
    @Override
    public ResponseEntity<?> getTravelMemberExpenses(Long tripId) {
        // 1. 여행 존재 여부 확인
        Trip trip = tripRepository.getTripById(tripId);

        // 2. 해당 여행의 개인별 지출 정보 조회
        List<PersonalSpending> spendingList = personalSpendingRepository.findByTrip(trip);
        List<Member> memberList = trip.getMemberList().stream().map(TripMember::getMember).toList();

        // 3. 여행 총 지출액 계산 (모든 지출 합산)
        int travelTotalAmount = spendingList.stream()
                .mapToInt(PersonalSpending::getAmount)
                .sum();

        // 4. 회원별 총 지출액 계산을 위한 Map 초기화
        Map<Member, Integer> memberExpenseMap = calculateMemberExpense(spendingList);

        // 5. DTO 변환(지출액 내림차순 정렬)
        List<MemberTotalExpenseRes> memberTotalExpenses = memberList.stream()
                .map(member -> MemberTotalExpenseRes.of(member, memberExpenseMap.getOrDefault(member, 0)))
                .sorted(Comparator.comparingInt(MemberTotalExpenseRes::amount).reversed()) // 지출액 내림차순 정렬
                .toList();

        return ResponseEntity.ok(
                ApiResponse.onSuccess(
                        TravelTotalExpense.of(travelTotalAmount, memberList.size(), memberTotalExpenses)));
    }

    /**
     * 카테고리별 지출 리포트
     */
    @Override
    public ResponseEntity<?> getCategoryReport(Long tripId) {
        // 1. 여행 존재 여부 확인
        Trip trip = tripRepository.getTripById(tripId);

        // 2. 여행 연관 지출 조회, 카테고리로 분류
        List<Expense> expenses = expenseRepository.findByTrip(trip);
        int totalExpense = expenses.stream().mapToInt(Expense::getPrice).sum();

        // 3. 카테고리별 지출액 계산
        Map<String, Integer> expenseMap = new HashMap<>();
        for (Expense expense : expenses) {
            String categoryName = expense.getCategory().toString();
            // defaultValue : 0 -> NullPointerException 방지
            expenseMap.put(categoryName, expenseMap.getOrDefault(categoryName, 0) + expense.getPrice());
        }

        // 4. DTO 변환
        List<CategoryExpenseRes> resultDto = expenseMap.entrySet().stream()
                .map(entry -> {
                    String categoryName = entry.getKey();
                    Integer categoryAmount = entry.getValue();
                    double percentage = ((double) categoryAmount / totalExpense) * 100; // 퍼센트 변환
                    return CategoryExpenseRes.of(categoryName, percentage, categoryAmount);
                })
                .sorted(Comparator.comparingDouble(CategoryExpenseRes::percentage).reversed()) // 퍼센트 기준 내림차순 정렬
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(resultDto));
    }

    /**
     * 회원별 카테고리 지출 리포트
     */
    @Override
    public ResponseEntity<?> getMemberCategoryReport(Long tripId, Category category) {
        Trip trip = tripRepository.getTripById(tripId);
        List<PersonalSpending> personalSpendingList = personalSpendingRepository.findByTripAndCategory(trip, category);
        List<Member> memberList = trip.getMemberList().stream().map(TripMember::getMember).toList();
        Map<Member, Integer> memberExpenseMap = new HashMap<>();
        for (Member member : memberList) {
            for (PersonalSpending personalSpending : personalSpendingList) {
                Member sender = personalSpending.getSender();
                Integer amount = personalSpending.getAmount();
                if (sender.equals(member)) { // 내가 보낸 돈인 경우 지출에 해당
                    memberExpenseMap.put(sender, memberExpenseMap.getOrDefault(sender, 0) + amount);
                }
            }
        }

        // 4. DTO 변환 및 지출액 내림차순 정렬
        List<MemberCategoryExpenseRes> resultDto = memberExpenseMap.entrySet().stream()
                .map(entry -> MemberCategoryExpenseRes.of(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparingInt(MemberCategoryExpenseRes::amount).reversed())
                .toList();
        return ResponseEntity.ok(ApiResponse.onSuccess(resultDto));
    }


    public Map<Member, Integer> calculateMemberExpense(List<PersonalSpending> personalSpendingList) {
        Map<Member, Integer> memberExpenseMap = new HashMap<>();
        for (PersonalSpending personalSpending : personalSpendingList) {
            Member sender = personalSpending.getSender();
            Member receiver = personalSpending.getReceiver();
            Integer amount = personalSpending.getAmount();
            // 본인 부담금 (개인 지출)
            if (sender.equals(receiver)) {
                memberExpenseMap.put(sender, memberExpenseMap.getOrDefault(sender, 0) + amount);
                continue;
            }
            // 수신자가 받았을 경우, 지출 금액 감소
            else if (memberExpenseMap.containsKey(receiver)) {
                memberExpenseMap.put(receiver, memberExpenseMap.get(receiver) - amount);
            }
            // 송신자가 보냈을 경우, 지출 금액 증가
            memberExpenseMap.put(sender, memberExpenseMap.getOrDefault(sender, 0) + amount);
        }
        return memberExpenseMap;
    }

    /**
     * 날짜별 총 지출 리포트
     */
    @Override
    public ResponseEntity<?> getDailyExpenseReport(Long tripId) {
        // 1. 여행 존재 여부 확인
        Trip trip = tripRepository.getTripById(tripId);

        // 2. 여행의 시작 날짜 & 종료 날짜 가져오기
        LocalDate startDate = trip.getStartDate();
        LocalDate endDate = trip.getEndDate();

        // 3. 해당 여행에서 발생한 모든 지출 조회
        List<Expense> expenses = expenseRepository.findByTrip(trip);

        // 4. 날짜별 지출을 그룹화하여 총합 계산
        Map<Integer, Integer> dailyExpenseMap = new TreeMap<>(); // 1일차부터 오름차순 정렬

        for (Expense expense : expenses) {
            LocalDate expenseDate = expense.getExpenseDate().toLocalDate(); // 지출 발생 날짜
            long daysBetween = ChronoUnit.DAYS.between(startDate, expenseDate); // 0일부터 시작
            int dayIndex = (int) daysBetween + 1; // 1일차부터 시작

            // 날짜별 지출 금액 누적
            dailyExpenseMap.put(dayIndex, dailyExpenseMap.getOrDefault(dayIndex, 0) + expense.getPrice());
        }

        // 5. DTO 변환
        List<DailyExpenseRes> resultDto = dailyExpenseMap.entrySet().stream()
                .map(entry -> DailyExpenseRes.of(entry.getKey(), entry.getValue()))
                .toList();

        return ResponseEntity.ok(ApiResponse.onSuccess(resultDto));
    }



    // ** 검토
    // 내림차순 정렬 기준 : createdAt
    public Pageable getDescSortPageable(Pageable pageable) {
        return PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "createdAt")
        );
    }


    // S3 이미지 업로드
    public List<TripImage> uploadExpenseImages(List<MultipartFile> images) throws IOException {
        List<TripImage> tripImages = new ArrayList<>();
        if (images != null) {
            for (MultipartFile image : images) {
                String imageUrl = s3Service.uploadFile(image);
                tripImages.add(TripImage.toEntity(imageUrl, null)); // Expense는 나중에 설정
            }
        }
        return tripImages;
    }

    // S3 이미지 삭제
    @Transactional
    public void deleteS3Images(List<TripImage> tripImages) {
        for (TripImage tripImage : tripImages) {
            s3Service.deleteFileByURL(tripImage.getImageUrl());
        }
    }


    // 지출 기록, 이미지 저장 - 트랜잭션 분리
    @Transactional
    public void saveExpenseWithTransaction(Trip trip, ExpenseCreateReq expenseCreateReq,
                                           Member payer, List<TripImage> tripImages) {
        // 1. Expense 객체 생성 및 저장
        Expense expense = ExpenseCreateReq.of(expenseCreateReq, trip, payer);
        expenseRepository.save(expense);

        // 2. TripImage 객체 저장
        for (TripImage tripImage : tripImages) {
            tripImage.changeExpense(expense);
            tripImageRepository.save(tripImage);
        }

        // 3. 지출 제외 멤버 저장
        List<Member> excludedMembers = saveExcludedMember(expenseCreateReq.excludedMember(), expense);

        // 4. 정산 결과에 반영
        saveMemberSpending(trip, expense, payer, excludedMembers);
    }

    public List<Member> saveExcludedMember(List<String> excludedMemberEmails, Expense expense) {
        List<Member> excludedMembers = new ArrayList<>();
        if (excludedMemberEmails != null) {
            for (String excludedEmail : excludedMemberEmails) {
                // 3.1. 회원 존재 여부 확인
                Member excludedMember = memberRepository.getMemberByEmail(excludedEmail);
                excludedMembers.add(excludedMember);
            }
            excludedMemberRepository.saveAll(excludedMembers.stream()
                    .map(member -> ExcludedMember.toEntity(member, expense))
                    .collect(Collectors.toList()));
        }
        return excludedMembers;
    }


    /**
     * 각 회원의 개인 지출 기록
     */
    @Transactional
    public void saveMemberSpending(Trip trip, Expense expense, Member payer, List<Member> excludedMembers) {
        // 1. 여행 참여 멤버 조회
        List<TripMember> tripMembers = tripMemberRepository.findByTrip(trip);
        List<Member> participants = tripMembers.stream()
                .map(TripMember::getMember)
                .toList();

        // 2. 정산 대상 필터링 (제외 멤버 제거, 결제자는 별도 처리)
        List<Member> actualParticipants = participants.stream()
                .filter(member -> !excludedMembers.contains(member)) // 계산에 포함되는 회원만 생각
                .toList();

        // 정산할 회원이 없는 경우 예외 처리
        if (actualParticipants.isEmpty()) {
            throw new GeneralException(ErrorStatus._SETTLEMENT_MEMBER_NOT_FOUND);
        }

        // 3. 1인당 정산 금액 계산
        int totalAmount = expense.getPrice();
        int settlementAmount = totalAmount / (actualParticipants.size());
        int remainingAmount = totalAmount % (actualParticipants.size());

        // 4. 참여자들에게 정산 금액 저장
        for (Member participant : actualParticipants) {
            personalSpendingRepository.save(PersonalSpending.toEntity(trip,expense,settlementAmount, participant, payer));
        }

        // 5. 나머지 금액 발생시, 결제자가 부담
        if (remainingAmount > 0) {
            personalSpendingRepository.save(
                    PersonalSpending.toEntity(trip,expense,remainingAmount, payer, payer));
        }
    }


}
