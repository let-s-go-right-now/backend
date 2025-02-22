package com.lets.go.right.now.init.category;

import com.lets.go.right.now.domain.expense.entity.Category;
import com.lets.go.right.now.domain.expense.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1)
@RequiredArgsConstructor
public class CategoryDataLoader implements CommandLineRunner {
    private final CategoryRepository categoryRepository;

    @Override
    public void run(String... args) throws Exception {
        String[] categories = {"교통","식사","관광","쇼핑","숙소","기타"};

        for (String category : categories) {
            createCategory(category);
        }
    }

    public Category createCategory(String title) {
        Category category = Category.builder()
                .title(title)
                .build();
        return categoryRepository.save(category);
    }
}