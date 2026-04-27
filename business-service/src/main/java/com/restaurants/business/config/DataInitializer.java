package com.restaurants.business.config;

import com.restaurants.business.entity.PlatEntity;
import com.restaurants.business.entity.RestaurantEntity;
import com.restaurants.business.repository.PlatRepository;
import com.restaurants.business.repository.RestaurantRepository;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements ApplicationRunner {

    private final RestaurantRepository restaurantRepository;
    private final PlatRepository platRepository;

    public DataInitializer(RestaurantRepository restaurantRepository, PlatRepository platRepository) {
        this.restaurantRepository = restaurantRepository;
        this.platRepository = platRepository;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (restaurantRepository.count() > 0) {
            return;
        }

        // --- Restaurants ---
        RestaurantEntity campiglia = new RestaurantEntity();
        campiglia.setIdProprietaire(1001L);
        campiglia.setNom("Campiglia");
        campiglia.setAdresse("3322 Rue Courval, Trois-Rivières");
        campiglia.setLocalisationTexte("Centre ville, près du parc");
        campiglia.setTelephone("819-654-6546");
        campiglia.setCourriel("contact@campglia.ca");
        campiglia.setImageUrl("https://images.unsplash.com/photo-1555396273-367ea4eb4db5?w=400");
        campiglia.setTypeCuisine("Italien");
        campiglia.setNote(4.5);
        campiglia.setTempsLivraison("25-35 min");
        campiglia.setFraisLivraison(3.99);
        campiglia.setCommandeMinimum(15.0);
        RestaurantEntity r1 = restaurantRepository.save(campiglia);

        RestaurantEntity petitFood = new RestaurantEntity();
        petitFood.setIdProprietaire(1002L);
        petitFood.setNom("Le petit food");
        petitFood.setAdresse("1500 Boulevard des Forges, Trois-Rivières");
        petitFood.setLocalisationTexte("Route universitaire");
        petitFood.setTelephone("819-819-8190");
        petitFood.setCourriel("info@petitfood.ca");
        petitFood.setImageUrl("https://images.unsplash.com/photo-1466978913421-dad2ebd01d17?w=400");
        petitFood.setTypeCuisine("Fast Food");
        petitFood.setNote(4.2);
        petitFood.setTempsLivraison("20-30 min");
        petitFood.setFraisLivraison(2.99);
        petitFood.setCommandeMinimum(12.0);
        RestaurantEntity r2 = restaurantRepository.save(petitFood);

        RestaurantEntity sushiBar = new RestaurantEntity();
        sushiBar.setIdProprietaire(1003L);
        sushiBar.setNom("Sushi Bar");
        sushiBar.setAdresse("84 Rue des Ursulines, Trois-Rivières");
        sushiBar.setLocalisationTexte("Vieux Trois-Rivières");
        sushiBar.setTelephone("819-666-6666");
        sushiBar.setCourriel("contact@sushibar.ca");
        sushiBar.setImageUrl("https://images.unsplash.com/photo-1579871494447-9811cf80d66c?w=400");
        sushiBar.setTypeCuisine("Japonais");
        sushiBar.setNote(4.8);
        sushiBar.setTempsLivraison("30-45 min");
        sushiBar.setFraisLivraison(4.99);
        sushiBar.setCommandeMinimum(20.0);
        RestaurantEntity r3 = restaurantRepository.save(sushiBar);

        RestaurantEntity laviolette = new RestaurantEntity();
        laviolette.setIdProprietaire(1004L);
        laviolette.setNom("Laviolette");
        laviolette.setAdresse("200 Rue Laviolette, Trois-Rivières");
        laviolette.setLocalisationTexte("Centre-ville");
        laviolette.setTelephone("819-666-7777");
        laviolette.setCourriel("bonjour@laviolette.ca");
        laviolette.setImageUrl("https://images.unsplash.com/photo-1414235077428-338989a2e8c0?w=400");
        laviolette.setTypeCuisine("Québécois");
        laviolette.setNote(4.6);
        laviolette.setTempsLivraison("25-40 min");
        laviolette.setFraisLivraison(3.49);
        laviolette.setCommandeMinimum(18.0);
        RestaurantEntity r4 = restaurantRepository.save(laviolette);

        RestaurantEntity chezFatou = new RestaurantEntity();
        chezFatou.setIdProprietaire(1005L);
        chezFatou.setNom("Chez Fatou");
        chezFatou.setAdresse("1000 rue viger Trois-Rivières");
        chezFatou.setLocalisationTexte("Centre-ville");
        chezFatou.setTelephone("819-777-8888");
        chezFatou.setCourriel("resto@chezfatou.ca");
        chezFatou.setImageUrl("https://images.unsplash.com/photo-1565299585323-38d6b0865b47?w=400");
        chezFatou.setTypeCuisine("Africain");
        chezFatou.setNote(4.6);
        chezFatou.setTempsLivraison("25-40 min");
        chezFatou.setFraisLivraison(3.49);
        chezFatou.setCommandeMinimum(20.0);
        RestaurantEntity r5 = restaurantRepository.save(chezFatou);

        // --- Plats Campiglia ---
        savePlat("Pizza Margherita", "Pizza italienne classique avec sauce tomate et mozzarella", 14.99, "Pizza",
                "https://images.unsplash.com/photo-1574071318508-1cdbab80d002?w=300", true, r1.getId());
        savePlat("Pizza Pepperoni", "Pizza garnie de pepperoni épicé et de fromage fondant", 16.99, "Pizza",
                "https://images.unsplash.com/photo-1628840042765-356cda07504e?w=300", true, r1.getId());
        savePlat("Pâtes Carbonara", "Pâtes crémeuses avec pancetta et parmesan", 15.99, "Pâtes",
                "https://images.unsplash.com/photo-1612874742237-6526221588e3?w=300", true, r1.getId());

        // --- Plats Le petit food ---
        savePlat("Burger Classic", "Burger classique avec bœuf, laitue, tomate et oignons", 12.99, "Burgers",
                "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?w=300", true, r2.getId());
        savePlat("Burger Bacon Deluxe", "Burger avec bacon croustillant, cheddar et sauce maison", 15.99, "Burgers",
                "https://images.unsplash.com/photo-1553979459-d2229ba7433b?w=300", true, r2.getId());
        savePlat("Frites Maison", "Frites fraîches assaisonnées, cuites à la perfection", 4.99, "Accompagnements",
                "https://images.unsplash.com/photo-1573080496219-bb080dd4f877?w=300", true, r2.getId());

        // --- Plats Sushi Bar ---
        savePlat("Sushi Combo Deluxe", "Assortiment de 20 pièces de sushi premium", 24.99, "Combos",
                "https://images.unsplash.com/photo-1579584425555-c3ce17fd4351?w=300", true, r3.getId());
        savePlat("Ramen Tonkotsu", "Ramen traditionnel avec bouillon de porc et œuf mariné", 16.99, "Soupes",
                "https://images.unsplash.com/photo-1591814468924-caf88d1232e1?w=300", true, r3.getId());

        // --- Plats Laviolette ---
        savePlat("Poutine Traditionnelle", "Frites, fromage en grains et sauce brune maison", 10.99, "Classiques",
                "https://images.unsplash.com/photo-1585937421612-70a008356fbe?w=300", false, r4.getId());
        savePlat("Tourtière du Lac", "Tourtière traditionnelle du Lac-Saint-Jean aux viandes mélangées", 14.99, "Classiques",
                "https://images.unsplash.com/photo-1604908176997-125f25cc6f3d?w=300", true, r4.getId());
        savePlat("Soupe aux pois", "Soupe aux pois jaunes mijotée avec jambon fumé", 7.99, "Soupes",
                "https://images.unsplash.com/photo-1547592166-23ac45744acd?w=300", false, r4.getId());

        // --- Plats Chez Fatou ---
        savePlat("Poulet Yassa", "Poulet mariné aux oignons et citron, spécialité sénégalaise", 13.99, "Plats principaux",
                "https://images.unsplash.com/photo-1604329760661-e71dc83f8f26?w=300", true, r5.getId());
        savePlat("Mafé de bœuf", "Ragoût de bœuf à la sauce arachide, servi avec du riz", 14.99, "Plats principaux",
                "https://images.unsplash.com/photo-1555939594-58d7cb561ad1?w=300", true, r5.getId());
    }

    private void savePlat(String nom, String description, double prix, String categorie,
                          String imageUrl, boolean disponible, Long idRestaurant) {
        PlatEntity plat = new PlatEntity();
        plat.setNom(nom);
        plat.setDescription(description);
        plat.setPrix(prix);
        plat.setCategorie(categorie);
        plat.setImageUrl(imageUrl);
        plat.setDisponible(disponible);
        plat.setIdRestaurant(idRestaurant);
        platRepository.save(plat);
    }
}
