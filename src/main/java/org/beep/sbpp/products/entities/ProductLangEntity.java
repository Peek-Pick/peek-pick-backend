package org.beep.sbpp.products.entities;

/**
 * 언어별 공통 인터페이스
 */
public interface ProductLangEntity {
    String getName();
    void setName(String name);

    String getDescription();
    void setDescription(String description);

    String getIngredients();
    void setIngredients(String ingredients);

    String getAllergens();
    void setAllergens(String allergens);

    String getNutrition();
    void setNutrition(String nutrition);

    String getCategory();
    void setCategory(String category);

    String getVolume();
    void setVolume(String volume);

    ProductBaseEntity getProductBase();
    void setProductBase(ProductBaseEntity base);
}
