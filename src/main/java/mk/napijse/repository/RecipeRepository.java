package mk.napijse.repository;

import mk.napijse.model.Category;
import mk.napijse.model.Recipe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    List<Recipe> findAllByNameContainingIgnoreCase(String name);
    List<Recipe> findAllByCategory(Category category);
    List<Recipe> findAllByNameContainingIgnoreCaseAndCategory(String name, Category category);
}