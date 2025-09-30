package guru.qa.niffler.model;

import java.util.Random;

public class CategoryModel {

    private String categoryName;

    public CategoryModel(String categoryName){
        this.categoryName = categoryName;
    }

    public String getCategoryName() {
        return categoryName;
    }
}
