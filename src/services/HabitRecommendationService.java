// File: src/services/HabitRecommendationService.java
package services;

import models.Category;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Abstract base class for habit recommendations.
 * Demonstrates ABSTRACTION and POLYMORPHISM.
 */
abstract class RecommendationEngine {
    /**
     * Abstract method to get recommendations.
     * Must be implemented by subclasses (demonstrates abstraction).
     * 
     * @param category The category
     * @return List of recommended habit names
     */
    public abstract ArrayList<String> getRecommendations(Category category);
}

/**
 * Concrete implementation of recommendation engine.
 * Demonstrates INHERITANCE and METHOD OVERRIDING.
 */
class StandardRecommendationEngine extends RecommendationEngine {
    
    // Static data member (demonstrates static fields)
    private static final HashMap<Category, ArrayList<String>> 
        RECOMMENDATION_MAP = new HashMap<>();
    
    // Static initialization block
    static {
        initializeRecommendations();
    }
    
    /**
     * Initialize recommendation mappings.
     * Static method.
     */
    private static void initializeRecommendations() {
        // Health recommendations
        ArrayList<String> healthHabits = new ArrayList<>();
        healthHabits.add("Drink 8 glasses of water");
        healthHabits.add("Take vitamins");
        healthHabits.add("Sleep 7-8 hours");
        healthHabits.add("Eat vegetables with every meal");
        RECOMMENDATION_MAP.put(Category.HEALTH, healthHabits);
        
        // Fitness recommendations
        ArrayList<String> fitnessHabits = new ArrayList<>();
        fitnessHabits.add("30 minutes cardio");
        fitnessHabits.add("Strength training");
        fitnessHabits.add("Morning stretching");
        fitnessHabits.add("10,000 steps daily");
        RECOMMENDATION_MAP.put(Category.FITNESS, fitnessHabits);
        
        // Productivity recommendations
        ArrayList<String> productivityHabits = new ArrayList<>();
        productivityHabits.add("Plan your day");
        productivityHabits.add("Deep work session (2 hours)");
        productivityHabits.add("Review and reflect");
        productivityHabits.add("Clear your inbox");
        RECOMMENDATION_MAP.put(Category.PRODUCTIVITY, productivityHabits);
        
        // Learning recommendations
        ArrayList<String> learningHabits = new ArrayList<>();
        learningHabits.add("Read for 30 minutes");
        learningHabits.add("Learn a new skill");
        learningHabits.add("Watch educational video");
        learningHabits.add("Practice a language");
        RECOMMENDATION_MAP.put(Category.LEARNING, learningHabits);
        
        // Mental Health recommendations
        ArrayList<String> mentalHealthHabits = new ArrayList<>();
        mentalHealthHabits.add("Meditation (10 minutes)");
        mentalHealthHabits.add("Journaling");
        mentalHealthHabits.add("Gratitude practice");
        mentalHealthHabits.add("Deep breathing exercises");
        RECOMMENDATION_MAP.put(Category.MENTAL_HEALTH, mentalHealthHabits);
        
        // Spiritual recommendations
        ArrayList<String> spiritualHabits = new ArrayList<>();
        spiritualHabits.add("Prayer or reflection");
        spiritualHabits.add("Read spiritual texts");
        spiritualHabits.add("Mindfulness practice");
        spiritualHabits.add("Connect with nature");
        RECOMMENDATION_MAP.put(Category.SPIRITUAL, spiritualHabits);
    }
    
    /**
     * Override abstract method to provide recommendations.
     * Demonstrates METHOD OVERRIDING.
     * 
     * @param category The category
     * @return List of recommendations
     */
    @Override
    public ArrayList<String> getRecommendations(Category category) {
        return RECOMMENDATION_MAP.getOrDefault(category, new ArrayList<>());
    }
}

/**
 * Service for providing habit recommendations.
 * Demonstrates use of POLYMORPHISM (uses RecommendationEngine abstraction).
 */
public class HabitRecommendationService {
    
    // Private instance variable (Encapsulation)
    // Polymorphism: parent reference holds child object
    private RecommendationEngine engine;
    
    // Constructor
    public HabitRecommendationService() {
        // Polymorphism: assign child object to parent reference
        this.engine = new StandardRecommendationEngine();
    }
    
    /**
     * Get habit recommendations for a category.
     * Uses polymorphism to call overridden method.
     * 
     * @param category The category
     * @return List of recommended habits
     */
    public ArrayList<String> getRecommendationsForCategory(Category category) {
        // Polymorphic method call
        return engine.getRecommendations(category);
    }
}