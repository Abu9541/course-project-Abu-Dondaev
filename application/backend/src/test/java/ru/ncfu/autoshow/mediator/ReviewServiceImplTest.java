package ru.ncfu.autoshow.mediator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.ncfu.autoshow.dto.review.ReviewRequest;
import ru.ncfu.autoshow.entity.Review;
import ru.ncfu.autoshow.entity.User;
import ru.ncfu.autoshow.entity.Vehicle;
import ru.ncfu.autoshow.exception.AccessForbiddenException;
import ru.ncfu.autoshow.exception.DuplicateResourceException;
import ru.ncfu.autoshow.foundation.ReviewRepository;
import ru.ncfu.autoshow.foundation.UserRepository;
import ru.ncfu.autoshow.foundation.VehicleRepository;
import ru.ncfu.autoshow.mapper.ReviewMapper;
import ru.ncfu.autoshow.mediator.impl.ReviewServiceImpl;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Сервис отзывов")
class ReviewServiceImplTest {

    @Mock ReviewRepository reviewRepository;
    @Mock UserRepository userRepository;
    @Mock VehicleRepository vehicleRepository;
    @Mock ReviewMapper reviewMapper;

    ReviewServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ReviewServiceImpl(reviewRepository, userRepository, vehicleRepository, reviewMapper);
    }

    private User userWithId(long id) {
        User u = new User();
        u.setId(id);
        return u;
    }

    private Review review(long ownerId) {
        return new Review(userWithId(ownerId), new Vehicle(), 5, "Отлично");
    }

    @Test
    @DisplayName("Повторный отзыв на тот же автомобиль отклоняется")
    void addDuplicateThrows() {
        when(reviewRepository.existsByUserIdAndVehicleId(4L, 1L)).thenReturn(true);
        assertThrows(DuplicateResourceException.class,
                () -> service.add(4L, 1L, new ReviewRequest(5, "x")));
    }

    @Test
    @DisplayName("Новый отзыв сохраняется")
    void addSuccessSaves() {
        when(reviewRepository.existsByUserIdAndVehicleId(4L, 1L)).thenReturn(false);
        when(userRepository.findById(4L)).thenReturn(Optional.of(userWithId(4L)));
        when(vehicleRepository.findById(1L)).thenReturn(Optional.of(new Vehicle()));

        service.add(4L, 1L, new ReviewRequest(5, "Отличный автомобиль"));

        verify(reviewRepository).save(any(Review.class));
    }

    @Test
    @DisplayName("Удаление чужого отзыва не-сотрудником запрещено (403)")
    void deleteForeignForbidden() {
        when(reviewRepository.findById(7L)).thenReturn(Optional.of(review(4L)));
        assertThrows(AccessForbiddenException.class, () -> service.delete(7L, 5L, false));
    }

    @Test
    @DisplayName("Сотрудник может удалить любой отзыв")
    void deleteByStaff() {
        Review r = review(4L);
        when(reviewRepository.findById(7L)).thenReturn(Optional.of(r));

        service.delete(7L, 99L, true);

        verify(reviewRepository).delete(r);
    }

    @Test
    @DisplayName("getByVehicle возвращает все отзывы автомобиля")
    void getByVehicleReturnsAll() {
        when(reviewRepository.findByVehicleIdOrderByCreatedAtDesc(1L))
                .thenReturn(List.of(review(4L), review(5L)));
        assertEquals(2, service.getByVehicle(1L).size());
    }
}
