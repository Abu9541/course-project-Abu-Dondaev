package ru.ncfu.autoshow.mediator;

import ru.ncfu.autoshow.dto.review.ReviewRequest;
import ru.ncfu.autoshow.dto.review.ReviewResponse;

import java.util.List;

/** Mediator: отзывы об автомобилях. */
public interface ReviewService {

    List<ReviewResponse> getByVehicle(Long vehicleId);

    ReviewResponse add(Long userId, Long vehicleId, ReviewRequest request);

    void delete(Long reviewId, Long actorId, boolean staff);
}
