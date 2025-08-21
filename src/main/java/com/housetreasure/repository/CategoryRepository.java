package com.housetreasure.repository;
import com.housetreasure.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
public interface CategoryRepository extends JpaRepository<Category, Long>  {

}
