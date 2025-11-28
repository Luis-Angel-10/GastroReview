package websiters.gastroreview;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.transaction.annotation.Transactional;
import jakarta.persistence.EntityManager;
import websiters.gastroreview.model.*;
import websiters.gastroreview.repository.*;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
class GastroReviewApplicationTests {

	@Autowired private UserRepository userRepository;
	@Autowired private UserProfileRepository userProfileRepository;
	@Autowired private RestaurantRepository restaurantRepository;
	@Autowired private AddressRepository addressRepository;
	@Autowired private RestaurantCategoryRepository categoryRepository;
	@Autowired private EntityManager entityManager;

	@Transactional
	@Test
	void fullDomainIntegrationTest() {

		User user = userRepository.save(
				User.builder()
						.email("testuser@example.com")
						.password("12345hash")
						.build()
		);

		userProfileRepository.save(
				UserProfile.builder()
						.user(user)
						.name("Test User")
						.bio("Usuario de prueba")
						.active(true)
						.build()
		);

		addressRepository.save(
				Address.builder()
						.street("Av. Central 123")
						.city("Monterrey")
						.stateRegion("Nuevo León")
						.postalCode(64000)
						.country("MX")
						.build()
		);

		Restaurant restaurant = restaurantRepository.save(
				Restaurant.builder()
						.name("La Parrilla del Norte")
						.description("Comida típica regiomontana")
						.phone("8123456789")
						.owner(user)
						.build()
		);

		RestaurantCategory category = categoryRepository.save(
				RestaurantCategory.builder()
						.name("Mexicana")
						.icon("🌮")
						.build()
		);
		category.getRestaurants().add(restaurant);
		categoryRepository.save(category);

		entityManager.flush();
		entityManager.clear();

		Restaurant loadedRestaurant = restaurantRepository.findById(restaurant.getId()).orElseThrow();
		User loadedOwner = loadedRestaurant.getOwner();

		assertThat(userRepository.findAll()).hasSize(1);
		assertThat(userProfileRepository.findAll()).hasSize(1);
		assertThat(restaurantRepository.findAll()).hasSize(1);
		assertThat(addressRepository.findAll()).hasSize(1);
		assertThat(categoryRepository.findAll()).hasSize(1);

		assertThat(loadedOwner).isNotNull();
		assertThat(loadedOwner.getEmail()).isEqualTo("testuser@example.com");

		RestaurantCategory loadedCategory = categoryRepository.findAll().get(0);
		assertThat(loadedCategory.getRestaurants()).isNotEmpty();

		System.out.println("\nUSUARIOS");
		userRepository.findAll().forEach(u ->
				System.out.println(" - " + u.getEmail())
		);

		System.out.println("\nPERFILES");
		userProfileRepository.findAll().forEach(p ->
				System.out.println(" - " + p.getName() + " (" + p.getUser().getEmail() + ")")
		);

		System.out.println("\nRESTAURANTES");
		restaurantRepository.findAll().forEach(r ->
				System.out.println(" - " + r.getName() + " (Dueño: " + r.getOwner().getEmail() + ")")
		);

		System.out.println("\nCATEGORÍAS");
		categoryRepository.findAll().forEach(c ->
				System.out.println(" - " + c.getName() + " → Restaurantes: " +
						c.getRestaurants().stream()
								.map(Restaurant::getName)
								.toList()
				)
		);
	}
}
