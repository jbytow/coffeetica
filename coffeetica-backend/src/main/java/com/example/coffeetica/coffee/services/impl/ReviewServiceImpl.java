package com.example.coffeetica.coffee.services.impl;

import com.example.coffeetica.coffee.models.CoffeeEntity;
import com.example.coffeetica.coffee.models.ReviewDTO;
import com.example.coffeetica.coffee.models.ReviewEntity;
import com.example.coffeetica.coffee.models.ReviewRequestDTO;
import com.example.coffeetica.coffee.repositories.CoffeeRepository;
import com.example.coffeetica.coffee.repositories.ReviewRepository;
import com.example.coffeetica.coffee.services.ReviewService;
import com.example.coffeetica.user.models.UserEntity;
import com.example.coffeetica.user.repositories.UserRepository;
import com.example.coffeetica.user.security.JwtTokenProvider;
import com.example.coffeetica.user.security.SecurityService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository reviewRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CoffeeRepository coffeeRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Autowired
    private SecurityService securityService;

    @Override
    public List<ReviewDTO> findAllReviews() {
        return reviewRepository.findAll().stream()
                .map(entity -> {
                    ReviewDTO reviewDTO = modelMapper.map(entity, ReviewDTO.class);
                    reviewDTO.setUserId(entity.getUser().getId());
                    reviewDTO.setUserName(entity.getUser().getUsername());
                    reviewDTO.setCoffeeId(entity.getCoffee().getId());
                    return reviewDTO;
                })
                .collect(Collectors.toList());
    }

    @Override
    public Optional<ReviewDTO> findReviewById(Long id) {
        return reviewRepository.findById(id)
                .map(entity -> {
                    ReviewDTO reviewDTO = modelMapper.map(entity, ReviewDTO.class);
                    reviewDTO.setUserId(entity.getUser().getId());
                    reviewDTO.setUserName(entity.getUser().getUsername());
                    reviewDTO.setCoffeeId(entity.getCoffee().getId());
                    return reviewDTO;
                });
    }

    @Override
    public Optional<ReviewDTO> findReviewByUserAndCoffeeId(String token, Long coffeeId) {
        Long userId = getUserIdFromToken(token); // Pobranie userId na podstawie username

        return reviewRepository.findByUserIdAndCoffeeId(userId, coffeeId)
                .map(entity -> {
                    ReviewDTO reviewDTO = modelMapper.map(entity, ReviewDTO.class);
                    reviewDTO.setUserId(entity.getUser().getId());
                    reviewDTO.setUserName(entity.getUser().getUsername());
                    return reviewDTO;
                });
    }

    @Override
    public ReviewDTO saveReview(ReviewRequestDTO reviewRequestDTO) {

        // 1) Pobierz ID zalogowanego użytkownika z tokena (JWT)
        Long userId = securityService.getCurrentUserId();

        // 2) Wyszukaj usera w bazie
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 3) Wyszukaj kawę w bazie na podstawie coffeeId z requestu
        CoffeeEntity coffee = coffeeRepository.findById(reviewRequestDTO.getCoffeeId())
                .orElseThrow(() -> new RuntimeException("Coffee not found"));

        // 4) Zmapuj ReviewRequestDTO -> ReviewEntity
        ReviewEntity entity = modelMapper.map(reviewRequestDTO, ReviewEntity.class);

        // Ręcznie przypisz powiązania (user, coffee)
        entity.setUser(user);
        entity.setCoffee(coffee);

        // 5) Zapisz w repo
        ReviewEntity savedEntity = reviewRepository.save(entity);

        // 6) Zmapuj ReviewEntity -> ReviewDTO (do odpowiedzi)
        ReviewDTO savedReviewDTO = modelMapper.map(savedEntity, ReviewDTO.class);

        // ModelMapper nie zawsze przenosi ID relacji, więc często trzeba je uzupełnić:
        savedReviewDTO.setUserId(savedEntity.getUser().getId());
        savedReviewDTO.setUserName(savedEntity.getUser().getUsername());
        savedReviewDTO.setCoffeeId(savedEntity.getCoffee().getId());
        savedReviewDTO.setCreatedAt(savedEntity.getCreatedAt().toString());

        return savedReviewDTO;
    }

    @Override
    public ReviewDTO updateReview(Long id, ReviewDTO reviewDetails) {
        ReviewEntity entity = reviewRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Review not found"));
        modelMapper.map(reviewDetails, entity);
        entity.setUser(userRepository.findById(reviewDetails.getUserId())
                .orElseThrow(() -> new RuntimeException("User not found")));
        entity.setCoffee(coffeeRepository.findById(reviewDetails.getCoffeeId())
                .orElseThrow(() -> new RuntimeException("Coffee not found")));
        ReviewEntity updatedEntity = reviewRepository.save(entity);
        return modelMapper.map(updatedEntity, ReviewDTO.class);
    }

    @Override
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }

    private Long getUserIdFromToken(String token) {
        String username = jwtTokenProvider.getIdentifierFromJWT(token.replace("Bearer ", ""));
        UserEntity user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return user.getId();
    }
}
