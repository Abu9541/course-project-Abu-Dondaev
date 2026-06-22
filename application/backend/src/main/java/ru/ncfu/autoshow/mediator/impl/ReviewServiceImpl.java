package ru.ncfu.autoshow.mediator.impl;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.ncfu.autoshow.dto.review.ReviewRequest;
import ru.ncfu.autoshow.dto.review.ReviewResponse;
import ru.ncfu.autoshow.entity.Review;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.exception.AccessForbiddenException;
import ru.ncfu.autoshow.exception.DuplicateResourceException;
import ru.ncfu.autoshow.exception.ResourceNotFoundException;
import ru.ncfu.autoshow.foundation.ReviewRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.ReviewMapper;
import ru.ncfu.autoshow.mediator.ReviewService;

import java.util.List;

@Service
@Transactional
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;
    private final ReviewMapper reviewMapper;

    public ReviewServiceImpl(ReviewRepository reviewRepository, UserRepository userRepository,
                             VehicleRepository vehicleRepository, ReviewMapper reviewMapper) {
        this.reviewRepository = reviewRepository;
        this.userRepository = userRepository;
        this.vehicleRepository = vehicleRepository;
        this.reviewMapper = reviewMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ReviewResponse> getByVehicle(Long vehicleId) {
        return reviewRepository.findByVehicleIdOrderByCreatedAtDesc(vehicleId)
                .stream().map(reviewMapper::toResponse).toList();
    }

    @Override
    public ReviewResponse add(Long userId, Long vehicleId, ReviewRequest request) {
        if (reviewRepository.existsByUserIdAndVehicleId(userId, vehicleId)) {
            throw new DuplicateResourceException("Вы уже оставляли отзыв на этот автомобиль");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", userId));
        Vehicle vehicle = vehicleRepository.findById(vehicleId)
                .orElseThrow(() -> new ResourceNotFoundException("Автомобиль", vehicleId));
        Review review = new Review(user, vehicle, request.rating(), request.comment());
        return reviewMapper.toResponse(reviewRepository.save(review));
    }

    @Override
    public void delete(Long reviewId, Long actorId, boolean staff) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new ResourceNotFoundException("Отзыв", reviewId));
        if (!staff && !review.getUser().getId().equals(actorId)) {
            throw new AccessForbiddenException("Отзыв принадлежит другому пользователю");
        }
        reviewRepository.delete(review);
    }
}
