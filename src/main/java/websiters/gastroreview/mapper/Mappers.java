package websiters.gastroreview.mapper;

import websiters.gastroreview.dto.*;
import websiters.gastroreview.model.*;

import java.util.Optional;
import java.util.stream.Collectors;
import java.util.Collections;

public final class Mappers {

    private Mappers() {}

    public static UserResponse toResponse(User entity) {
        if (entity == null) return null;

        return UserResponse.builder()
                .id(entity.getId())
                .email(entity.getEmail())
                .roles(
                        entity.getRoles() != null
                                ? entity.getRoles().stream()
                                .map(Role::getName)
                                .collect(Collectors.toSet())
                                : Collections.emptySet()
                )
                .build();
    }

    public static UserProfileResponse toResponse(UserProfile entity) {
        if (entity == null) return null;

        return UserProfileResponse.builder()
                .user_Id(entity.getUserId())
                .photo_Url(Optional.ofNullable(entity.getPhotoUrl()).orElse(""))
                .name(Optional.ofNullable(entity.getName()).orElse(""))
                .active(entity.isActive())
                .createdAt(entity.getCreatedAt())
                .bio(Optional.ofNullable(entity.getBio()).orElse(""))
                .build();
    }

    public static RoleResponse toResponse(Role entity) {
        if (entity == null) return null;

        return RoleResponse.builder()
                .id(entity.getId())
                .name(Optional.ofNullable(entity.getName()).orElse(""))
                .description(Optional.ofNullable(entity.getDescription()).orElse(""))
                .build();
    }

    public static RestaurantResponse toResponse(Restaurant entity) {
        if (entity == null) return null;

        return RestaurantResponse.builder()
                .id(entity.getId())
                .name(Optional.ofNullable(entity.getName()).orElse(""))
                .description(Optional.ofNullable(entity.getDescription()).orElse(""))
                .phone(Optional.ofNullable(entity.getPhone()).orElse(""))
                .email(Optional.ofNullable(entity.getEmail()).orElse(""))
                .owner_Id(entity.getOwner() != null ? entity.getOwner().getId() : null)
                .build();
    }

    public static AddressResponse toResponse(Address entity) {
        if (entity == null) return null;

        return AddressResponse.builder()
                .id(entity.getId())
                .street(Optional.ofNullable(entity.getStreet()).orElse(""))
                .site(Optional.ofNullable(entity.getSite()).orElse(""))
                .neighborhood(Optional.ofNullable(entity.getNeighborhood()).orElse(""))
                .city(Optional.ofNullable(entity.getCity()).orElse(""))
                .state_region(Optional.ofNullable(entity.getStateRegion()).orElse(""))
                .postal_code(entity.getPostalCode())
                .country(Optional.ofNullable(entity.getCountry()).orElse(""))
                .latitude(entity.getLatitude())
                .longitude(entity.getLongitude())
                .build();
    }

    public static CategoryResponse toResponse(RestaurantCategory entity) {
        if (entity == null) return null;

        return CategoryResponse.builder()
                .id(entity.getId())
                .name(Optional.ofNullable(entity.getName()).orElse(""))
                .icon(Optional.ofNullable(entity.getIcon()).orElse(""))
                .build();
    }

    public static UserPreferenceResponse toResponse(UserPreference entity) {
        if (entity == null) return null;

        return new UserPreferenceResponse(
                entity.getId(),
                Optional.ofNullable(entity.getPrefKey()).orElse(""),
                entity.getValue()
        );
    }

    public static UserAchievementResponse toResponse(UserAchievement entity) {
        if (entity == null) return null;

        return new UserAchievementResponse(
                entity.getId(),
                Optional.ofNullable(entity.getBadge()).orElse(""),
                Optional.ofNullable(entity.getLevel()).orElse(1),
                Optional.ofNullable(entity.getStars()).orElse(0)
        );
    }

    public static UserRoleResponse toResponse(UserRole entity) {
        if (entity == null) return null;

        return new UserRoleResponse(
                entity.getId().getUserId(),
                entity.getId().getRoleId(),
                entity.getAssignedAt()
        );
    }

    public static FriendshipResponse toResponse(Friendship entity) {
        if (entity == null) return null;

        return new FriendshipResponse(
                entity.getId().getFollowerId(),
                entity.getId().getFollowedId(),
                entity.getCreatedAt()
        );
    }

    public static RestaurantAddressResponse toResponse(RestaurantAddress entity) {
        if (entity == null) return null;

        return new RestaurantAddressResponse(
                entity.getId().getRestaurantId(),
                entity.getId().getAddressId(),
                entity.isPrimary(),
                Optional.ofNullable(entity.getBranchName()).orElse("")
        );
    }

    public static DishResponse toResponse(Dish entity) {
        if (entity == null) return null;

        return DishResponse.builder()
                .id(entity.getId())
                .name(Optional.ofNullable(entity.getName()).orElse(""))
                .description(Optional.ofNullable(entity.getDescription()).orElse(""))
                .priceCents(entity.getPriceCents())
                .available(entity.getAvailable())
                .restaurantId(entity.getRestaurant() != null ? entity.getRestaurant().getId() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static RestaurantScheduleResponse toResponse(RestaurantSchedule entity) {
        if (entity == null) return null;

        return RestaurantScheduleResponse.builder()
                .id(entity.getId())
                .restaurantId(entity.getRestaurant() != null ? entity.getRestaurant().getId() : null)
                .weekday(entity.getWeekday())
                .openTime(entity.getOpenTime())
                .closeTime(entity.getCloseTime())
                .special(entity.getSpecial())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static RestaurantImageResponse toResponse(RestaurantImage entity) {
        if (entity == null) return null;

        return RestaurantImageResponse.builder()
                .id(entity.getId())
                .restaurantId(entity.getRestaurant() != null ? entity.getRestaurant().getId() : null)
                .url(Optional.ofNullable(entity.getUrl()).orElse(""))
                .altText(Optional.ofNullable(entity.getAltText()).orElse(""))
                .uploadedBy(entity.getUploadedBy() != null ? entity.getUploadedBy().getId() : null)
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ReviewResponse toResponse(Review entity) {
        if (entity == null) return null;

        return ReviewResponse.builder()
                .id(entity.getId())
                .userId(entity.getAuthor() != null ? entity.getAuthor().getId() : null)
                .restaurantId(entity.getRestaurant() != null ? entity.getRestaurant().getId() : null)
                .dishId(entity.getDish() != null ? entity.getDish().getId() : null)
                .title(Optional.ofNullable(entity.getTitle()).orElse(""))
                .content(Optional.ofNullable(entity.getContent()).orElse(""))
                .hasAudio(entity.isHasAudio())
                .hasImage(entity.isHasImage())
                .publishedAt(entity.getPublishedAt())
                .build();
    }



    public static ReviewImageResponse toResponse(ReviewImage entity) {
        if (entity == null) return null;

        return ReviewImageResponse.builder()
                .id(entity.getId())
                .reviewId(entity.getReview() != null ? entity.getReview().getId() : null)
                .url(Optional.ofNullable(entity.getUrl()).orElse(""))
                .altText(Optional.ofNullable(entity.getAltText()).orElse(""))
                .build();
    }

    public static ReviewAudioResponse toResponse(ReviewAudio entity) {
        if (entity == null) return null;

        return ReviewAudioResponse.builder()
                .id(entity.getId())
                .reviewId(entity.getReview() != null ? entity.getReview().getId() : null)
                .url(Optional.ofNullable(entity.getUrl()).orElse(""))
                .durationSeconds(entity.getDurationSeconds())
                .transcription(Optional.ofNullable(entity.getTranscription()).orElse(""))
                .build();
    }

    public static ReviewCommentResponse toResponse(ReviewComment entity) {
        if (entity == null) return null;

        return ReviewCommentResponse.builder()
                .id(entity.getId())
                .reviewId(entity.getReview() != null ? entity.getReview().getId() : null)
                .authorId(entity.getAuthor() != null ? entity.getAuthor().getId() : null)
                .parentId(entity.getParent() != null ? entity.getParent().getId() : null)
                .content(Optional.ofNullable(entity.getContent()).orElse(""))
                .publishedAt(entity.getPublishedAt())
                .build();
    }

    public static FavoriteRestaurantResponse toResponse(FavoriteRestaurant entity) {
        if (entity == null) return null;

        return FavoriteRestaurantResponse.builder()
                .userId(entity.getId().getUserId())
                .restaurantId(entity.getId().getRestaurantId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static FavoriteReviewResponse toResponse(FavoriteReview entity) {
        if (entity == null) return null;

        return FavoriteReviewResponse.builder()
                .userId(entity.getId().getUserId())
                .reviewId(entity.getId().getReviewId())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static AlertResponse toResponse(Alert entity) {
        if (entity == null) return null;

        return AlertResponse.builder()
                .id(entity.getId())
                .type(Optional.ofNullable(entity.getType()).orElse(""))
                .restaurantId(entity.getRestaurant() != null ? entity.getRestaurant().getId() : null)
                .reviewId(entity.getReview() != null ? entity.getReview().getId() : null)
                .detail(Optional.ofNullable(entity.getDetail()).orElse(""))
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static NotificationResponse toResponse(Notification entity) {
        if (entity == null) return null;

        return NotificationResponse.builder()
                .id(entity.getId())
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .type(Optional.ofNullable(entity.getType()).orElse(""))
                .message(Optional.ofNullable(entity.getMessage()).orElse(""))
                .read(entity.isRead())
                .referenceId(entity.getReferenceId())
                .metadata(entity.getMetadata())
                .build();
    }

    public static Dish toDishEntity(DishRequest dto, Restaurant restaurant) {
        if (dto == null || restaurant == null) return null;

        return Dish.builder()
                .id(null) // se genera con @PrePersist
                .restaurant(restaurant)
                .name(dto.getName())
                .description(dto.getDescription())
                .priceCents(dto.getPriceCents())
                .available(dto.getAvailable() != null ? dto.getAvailable() : true)
                .build();
    }

    public static RestaurantSchedule toRestaurantScheduleEntity(RestaurantScheduleRequest dto, Restaurant restaurant) {
        if (dto == null || restaurant == null) return null;

        return RestaurantSchedule.builder()
                .id(null)
                .restaurant(restaurant)
                .weekday(dto.getWeekday())
                .openTime(dto.getOpenTime())
                .closeTime(dto.getCloseTime())
                .special(dto.getSpecial() != null ? dto.getSpecial() : false)
                .build();
    }

    public static Review toReviewEntity(ReviewRequest dto, User author, Restaurant restaurant, Dish dish) {
        if (dto == null || author == null || restaurant == null) return null;

        return Review.builder()
                .id(null)
                .author(author)
                .restaurant(restaurant)
                .dish(dish)
                .title(dto.getTitle())
                .content(dto.getContent())
                .hasAudio(dto.getHasAudio() != null ? dto.getHasAudio() : false)
                .hasImage(dto.getHasImage() != null ? dto.getHasImage() : false)
                .build();
    }

    public static ReviewImage toReviewImageEntity(ReviewImageRequest dto, Review review) {
        if (dto == null || review == null) return null;

        return ReviewImage.builder()
                .id(null)
                .review(review)
                .url(dto.getUrl())
                .altText(dto.getAltText())
                .build();
    }

    public static ReviewComment toReviewCommentEntity(
            ReviewCommentRequest dto,
            User author,
            Review review,
            ReviewComment parent
    ) {
        if (dto == null || author == null || review == null) return null;

        return ReviewComment.builder()
                .author(author)
                .review(review)
                .parent(parent)
                .content(dto.getContent())
                .build();
    }


    public static ReviewComment toReviewCommentEntity(ReviewCommentRequest dto, Review review, User author, ReviewComment parent) {
        if (dto == null || review == null || author == null) return null;

        return ReviewComment.builder()
                .id(null)
                .review(review)
                .author(author)
                .parent(parent)
                .content(dto.getContent())
                .build();
    }

    public static Alert toAlertEntity(AlertRequest dto, Restaurant restaurant, Review review) {
        if (dto == null) return null;

        return Alert.builder()
                .id(null)
                .type(dto.getType())
                .restaurant(restaurant)
                .review(review)
                .detail(dto.getDetail())
                .build();
    }

    public static Notification toNotificationEntity(NotificationRequest dto, User user) {
        if (dto == null || user == null) return null;

        return Notification.builder()
                .id(null)
                .user(user)
                .type(dto.getType())
                .message(dto.getMessage())
                .read(false)
                .referenceId(dto.getReferenceId())
                .metadata(dto.getMetadata())
                .build();
    }

    public static FavoriteRestaurant toFavoriteRestaurantEntity(FavoriteRestaurantRequest dto, User user, Restaurant restaurant) {
        if (dto == null || user == null || restaurant == null) return null;

        return FavoriteRestaurant.builder()
                .id(new FavoriteRestaurantId(user.getId(), restaurant.getId()))
                .user(user)
                .restaurant(restaurant)
                .build();
    }

    public static RestaurantImage toRestaurantImageEntity(RestaurantImageRequest dto, Restaurant restaurant, User uploader) {
        if (dto == null || restaurant == null) return null;

        return RestaurantImage.builder()
                .id(null)
                .restaurant(restaurant)
                .url(dto.getUrl())
                .altText(dto.getAltText())
                .uploadedBy(uploader)
                .build();
    }

    public static Rating toRatingEntity(RatingRequest dto, User user, Review review) {
        if (dto == null || user == null || review == null) return null;

        return Rating.builder()
                .user(user)
                .review(review)
                .stars(dto.getStars())
                .points(dto.getPoints() != null ? dto.getPoints() : 0)
                .build();
    }


    public static RatingResponse toResponse(Rating entity) {
        if (entity == null) return null;

        return RatingResponse.builder()
                .id(entity.getId())
                .reviewId(entity.getReview() != null ? entity.getReview().getId() : null)
                .userId(entity.getUser() != null ? entity.getUser().getId() : null)
                .stars(entity.getStars())
                .points(entity.getPoints())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public static ReviewAudio toReviewAudioEntity(ReviewAudioRequest dto, Review review) {
        if (dto == null || review == null) return null;

        return ReviewAudio.builder()
                .review(review)
                .url(dto.getUrl())
                .durationSeconds(dto.getDurationSeconds())
                .transcription(dto.getTranscription())
                .build();
    }



}
