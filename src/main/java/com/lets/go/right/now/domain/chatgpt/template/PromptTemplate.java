package com.lets.go.right.now.domain.chatgpt.template;

import com.lets.go.right.now.domain.chatgpt.dto.ChatGptReq;
import org.springframework.stereotype.Component;

@Component
public class PromptTemplate {

    private final String promptText = """
            지금부터 너는 나의 국내 여행지 및 상세 일정을 작성해주는 여행 플래너야.
                            #내 정보를 바탕으로 #조건과 #주의 사항에 맞춰 #예시에 나오는 모든 필드를 반드시 포함한 상태의 json 형식 데이터로 여행지 1곳을 추천해줘.
                       \s
                            # 내 정보
                            (1) 출발지 : %s
                            (2) 여행 기간 : %s부터 %s까지
                            (3) 여행에 쓸 수 있는 최대 예산 : %d원
                            (4) 교통 수단 : %s
                       \s
                            # 조건
                            (1) title : 해당 여행 일정을 표현하는 제목
                            (2) place : 해당 여행 일정 중 가장 유명한 명소
                            (3) description : 해당 여행 일정에 대한 전반적인 1줄 설명 ( ~2박 3일 여행 이런식으로 작성하지마)
                            (4) transportation : 해당 여행 중 이용할 교통 수단 (최대 2개까지만 작성, 2개 작성 시 + 로 표현)
                            (5) cost : 여행 총 경비 (int형 값으로 작성)
                            (6) itinerary : 여행 시작 일자부터 종료 일자까지의 모든 날의 일정
                       \s
                            # 주의 사항
                            (1) 여행 첫 날 오전은 숙소에 도착하는 일정이다.
                            (3) 여행 마지막 날을 제외하고 모든 날짜의 저녁은 숙소에 도착하는 일정이다.
                            (4) 여행 마지막 날 저녁은 출발지로 돌아가는 일정이다.
                            (5) itinerary의 date는 MM.DD 형식이다.
                            (6) link는 https://search.naver.com/search.naver?where=nexearch&sm=top_hty&fbm=0&ie=utf8&query={location} 형태로 제공한다.
                            (7) cost가 무료이면 0으로 표시한다.
                            (8) type이 attraction인 location인 경우에만 hashtags 제공 한다. (단, 숙소는 제외)
                            (9) 장소를 이동할 때는 교통 수단이 무조건 존재한다.
                            (10) #내 정보의 교통수단이 대중교통이면 모든 교통 수단 이용 가능하고 자가용이면 자가용과 도보 중 최소 1개 이상 이용 가능하다.
            		        (11) 반드시 #예시의 필드를 모든 날짜가 동일한 형태로 갖도록 하고 type의 값에 따라 추천 코스를 작성해야 한다.
            
            # 예시
            [
              {
                "title": "산과 바다가 만나는 속초",
                "place": "속초 바다",
                "description": "강릉과 속초는 겨울에도 매력적인 바다와 산을 동시에 즐길 수 있는 곳입니다.",
                "cost": 350000,
                "transportation": "버스 + 택시",
                "itinerary": [
                  {
                    "day": "1일차",
                    "date": "12.12",
                    "title": "맛과 멋으로 시작하는 속초 여행",
                    "destination": "라마다호텔 속초",
                    "schedule": [
                      {
                        "time": "오전",
                        "departure": "서울시 중동",
                        "events": [
                          {
                            "type": "eat",
                            "location": "터미널에서 브런치",
                            "details": "속초로 출발하기 전, 동서울종합터미널 근처에서 간단하게 아침 식사를 해결하시면 좋습니다. 터미널 주변에는 다양한 식당과 카페가 위치해 있어 선택의 폭이 넓어요.",
                            "cost": 10000
                          },
                          {
                            "type": "transport",
                            "location": "속초로 이동",
                            "details": "동서울종합터미널에서 속초행 고속버스를 탑승합니다.",
                            "from": "동서울종합터미널",
                            "to": "속초고속버스터미널",
                            "options": [
                              {
                                "mode": "고속버스",
                                "duration": "2시간 30분",
                                "cost": 22000
                              }
                            ]
                          },
                          {
                            "type": "transport",
                            "from": "속초고속버스터미널",
                            "to": "라마다호텔 속초",
                            "options": [
                              {
                                "mode": "자동차",
                                "duration": "5분",
                                "cost": 5000
                              },
                              {
                                "mode": "버스",
                                "duration": "10분",
                                "cost": 1500
                              },
                              {
                                "mode": "도보",
                                "duration": "5분"
                              }
                            ]
                          },
                          {
                            "type": "attraction",
                            "location": "라마다호텔 속초",
                            "details": "속초 해변 인근에 위치한 4성급 호텔로, 깔끔한 시설과 바다 전망을 제공합니다.",
                            "cost": 80000,
                            "link": ""
                          }
                        ]
                      },
                      {
                        "time": "오후",
                        "departure": "라마다호텔 속초",
                        "events": [
                          {
                            "type": "transport",
                            "from": "라마다호텔 속초",
                            "to": "속초중앙시장",
                            "options": [
                              {
                                "mode": "자동차",
                                "duration": "10분",
                                "cost": 7000
                              },
                              {
                                "mode": "버스",
                                "duration": "15분",
                                "cost": 1500
                              },
                              {
                                "mode": "도보",
                                "duration": "40분"
                              }
                            ]
                          },
                          {
                            "type": "attraction",
                            "location": "속초 중앙시장",
                            "details": "속초의 대표적인 전통시장으로, 다양한 해산물과 지역 특산품을 경험할 수 있습니다.",
                            "cost": 10000,
                            "link": "",
                            "hashtags": ["#전통시장", "#해산물", "#지역특산물"]
                          },
                          {
                            "type": "transport",
                            "from": "속초중앙시장",
                            "to": "88생선구이",
                            "options": [
                              {
                                "mode": "도보",
                                "duration": "5분"
                              }
                            ]
                          },
                          {
                            "type": "eat",
                            "location": "88생선구이",
                            "details": "속초 중앙시장 인근에 위치한 유명한 생선구이 전문점으로, 신선한 생선을 정성껏 구워 제공합니다.",
                            "cost": 16000,
                            "link": ""
                          }
                        ]
                      },
                      {
                        "time": "저녁",
                        "departure": "88생선구이",
                        "events": [
                          {
                            "type": "transport",
                            "from": "88생선구이",
                            "to": "속초해수욕장",
                            "options": [
                              {
                                "mode": "자동차",
                                "duration": "10분",
                                "cost": 7000
                              },
                              {
                                "mode": "버스",
                                "duration": "15분",
                                "cost": 1500
                              },
                              {
                                "mode": "도보",
                                "duration": "40분"
                              }
                            ]
                          },
                          {
                            "type": "attraction",
                            "location": "속초 해수욕장 야경 즐기기",
                            "details": "넓은 모래사장과 함께 해안가를 따라 조명이 잘 조성되어 있어 야경 산책에 제격입니다. 바다 바로 앞에 있는 카페 거리와 편의시설이 잘 갖추어져 있어 간단한 음료를 즐기며 휴식할 수 있습니다.",
                            "link": "",
                            "hashtags": ["#속초해수욕장", "#겨울바다", "#야경산책", "#낭만적인밤"]
                          },
                          {
                            "type": "transport",
                            "from": "속초해수욕장",
                            "to": "대포항",
                            "options": [
                              {
                                "mode": "자동차",
                                "duration": "5분",
                                "cost": 7000
                              },
                              {
                                "mode": "버스",
                                "duration": "10분",
                                "cost": 1500
                              },
                              {
                                "mode": "도보",
                                "duration": "25분"
                              }
                            ]
                          },
                          {
                            "type": "eat",
                            "location": "대포항 석식",
                            "details": "신선한 해산물을 맛볼 수 있으며, 특히 회가 유명합니다.",
                            "link": ""
                          },
                          {
                            "type": "transport",
                            "from": "대포항",
                            "to": "라마다호텔 속초",
                            "options": [
                              {
                                "mode": "자동차",
                                "duration": "5분",
                                "cost": 7000
                              },
                              {
                                "mode": "버스",
                                "duration": "10분",
                                "cost": 1500
                              },
                              {
                                "mode": "도보",
                                "duration": "25분"
                              }
                            ]
                          }
                        ]
                      }
                    ]
                  },
                  {
                    "destination": "라마다호텔 속초"
                  }
                ]
              }
            ]
            
            """;

    public String createPrompt(ChatGptReq chatGptReq) {
        return String.format(
                promptText,
                chatGptReq.departure(),
                chatGptReq.startDate(),
                chatGptReq.endDate(),
                chatGptReq.budget(),
                chatGptReq.transportMode()
        );
    }
}
