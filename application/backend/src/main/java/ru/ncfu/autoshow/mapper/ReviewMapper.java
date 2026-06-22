package ru.ncfu.autoshow.mapper;

import org.springframework.stereotype.Component;
import ru.ncfu.autoshow.dto.review.ReviewResponse;
import ru.ncfu.autoshow.entity.Review;

/** Data Mapper для отзывов. */
@Component
public class ReviewMapper {

    public ReviewResponse toResponse(Review r) {
        return new ReviewResponse(
                r.getId(),
                r.getUser() != null ? r.getUser().getId() : null,
                r.getUser() != null ? r.getUser().getFullName() : null,
                r.getVehicle() != null ? r.getVehicle().getId() : null,
                r.getRating(),
                r.getComment(),
                r.getCreatedAt()
        );
    }
}
