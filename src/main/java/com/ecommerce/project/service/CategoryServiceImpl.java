package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ApiException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements  CategoryService{

    //private List<Category> categories = new ArrayList<>();

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize,String sortBy,String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc")
                ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(pageNumber,pageSize,sortByAndOrder);
        Page<Category> pageableCategories = categoryRepository.findAll(pageable);
        List<Category> categories =pageableCategories.getContent();
        if (categories.isEmpty()){
            throw new ApiException("No categories is added till now");
        }
        List<CategoryDTO> categoryDTOS = categories.stream()
                .map(category -> modelMapper.map(category, CategoryDTO.class))
                .toList();
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(pageableCategories.getNumber());
        categoryResponse.setPageSize(pageableCategories.getSize());
        categoryResponse.setTotalElements(pageableCategories.getTotalElements());
        categoryResponse.setTotalPages(pageableCategories.getTotalPages());
        categoryResponse.setLastPage(pageableCategories.isLast());
        return categoryResponse;
    }

    @Override
    public CategoryDTO createCategoty(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);

        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        if(savedCategory!= null){
            throw new ApiException("Category with the name "+category.getCategoryName()+" already exists");
        }
        return modelMapper.map(categoryRepository.save(category), CategoryDTO.class);

    }

    @Override
    public CategoryDTO deleteCategory(Long categoryId) {
//        List<Category> categories = categoryRepository.findAll();
//        Category category = categories.stream()
//                .filter(c-> c.getCategoryId().equals(categoryId))
//                .findFirst().orElseThrow(()-> new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found"));
//        categoryRepository.delete(category);
//        return "category deleted";
        Category savedcategory = categoryRepository.findById(categoryId).orElseThrow(()->
                new  ResourceNotFoundException("Category","CategoryId",categoryId));
        categoryRepository.delete(savedcategory);
        return modelMapper.map(savedcategory, CategoryDTO.class);
    }

    @Override
    public CategoryDTO updateCategory(CategoryDTO categoryDTO,Long Id) {
//        List<Category> categories = categoryRepository.findAll();
//        Optional<Category> cat = categories.stream()
//                .filter(c-> c.getCategoryId().equals(Id))
//                .findFirst();
//        if(cat.isPresent()){
//            Category existingCategory = cat.get();
//            existingCategory.setCategoryName(category.getCategoryName());
//            return categoryRepository.save(existingCategory);
//        }else{
//            throw new ResponseStatusException(HttpStatus.NOT_FOUND,"Category not found");
//        }
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedcategory = categoryRepository.findById(Id).orElseThrow(()->
                        new ResourceNotFoundException("Category","CategoryId",Id));
        savedcategory.setCategoryName(category.getCategoryName());
        return modelMapper.map(categoryRepository.save(savedcategory), CategoryDTO.class);

    }
}
