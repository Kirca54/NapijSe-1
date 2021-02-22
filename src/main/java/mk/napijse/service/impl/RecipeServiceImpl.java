package mk.napijse.service.impl;

import mk.napijse.model.Category;
import mk.napijse.model.Recipe;
import mk.napijse.model.User;
import mk.napijse.model.exceptions.CategoryNotFoundException;
import mk.napijse.model.exceptions.RecipeNotFoundException;
import mk.napijse.repository.CategoryRepository;
import mk.napijse.repository.RecipeRepository;
import mk.napijse.repository.UserRepository;
import mk.napijse.service.RecipeService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RecipeServiceImpl implements RecipeService {

    private final RecipeRepository recipeRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    public RecipeServiceImpl(RecipeRepository recipeRepository,
                             CategoryRepository categoryRepository,
                             UserRepository userRepository) {
        this.recipeRepository = recipeRepository;
        this.categoryRepository = categoryRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Recipe> findAll() {
        return this.recipeRepository.findAll();
    }

    @Override
    public void deleteById(Long id) {
        this.recipeRepository.deleteById(id);
    }

    @Override
    public Optional<Recipe> findById(Long id) {
        return this.recipeRepository.findById(id);
    }

    @Override
    public Recipe editRecipe(Long id, String name, String description, String ingredients, Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        Recipe oldRecipe = this.findById(id).orElseThrow(RecipeNotFoundException::new);
        oldRecipe.setName(name);
        oldRecipe.setDescription(description);
        oldRecipe.setIngredients(ingredients);
        oldRecipe.setCategory(category);
        return this.recipeRepository.save(oldRecipe);
    }

    @Override
    public Recipe saveRecipe(String name, String description, String ingredients, Long categoryId, String username) {
        Category category = this.categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        User currentUser = this.userRepository.findByUsername(username).get();
        Recipe recipe = new Recipe(name, description, ingredients, currentUser, category);
        return this.recipeRepository.save(recipe);
    }

    @Override
    public List<Recipe> findAllFavourites(String username) {
        User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username));

        return this.userRepository.findByUsername(username).get().getFavourites();
    }

    @Override
    public Recipe addToFavourites(String username, Long recipeId) {
        User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username));
        Recipe recipe = this.recipeRepository.findById(recipeId).orElseThrow(RecipeNotFoundException::new);
        user.addToFavourites(recipe);
        this.userRepository.save(user);
        return recipe;
    }

    @Override
    public void deleteFromFavourites(String username, Long recipeId) {
        User user = this.userRepository
                .findByUsername(username)
                .orElseThrow(()->new UsernameNotFoundException(username));
        Recipe recipe = this.recipeRepository.findById(recipeId).orElseThrow(RecipeNotFoundException::new);
        user.deleteFromFavourites(recipe);
        this.userRepository.save(user);
    }

    @Override
    public List<Recipe> findAllByName(String name) {
        return this.recipeRepository.findAllByNameContainingIgnoreCase(name);
    }

    @Override
    public List<Recipe> findAllByCategory(Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        return this.recipeRepository.findAllByCategory(category);
    }

    @Override
    public List<Recipe> findAllByNameAndCategory(String name, Long categoryId) {
        Category category = this.categoryRepository.findById(categoryId).orElseThrow(CategoryNotFoundException::new);
        return this.recipeRepository.findAllByNameContainingIgnoreCaseAndCategory(name, category);
    }
}
