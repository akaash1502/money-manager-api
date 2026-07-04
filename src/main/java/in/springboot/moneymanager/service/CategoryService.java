package in.springboot.moneymanager.service;

import in.springboot.moneymanager.dto.CategoryDTO;
import in.springboot.moneymanager.entity.CategoryEntity;
import in.springboot.moneymanager.entity.ProfileEntity;
import in.springboot.moneymanager.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final ProfileService profileService;
    private final CategoryRepository categoryRepository;

    //save Category
    public CategoryDTO saveCategory(CategoryDTO categoryDTO){
        ProfileEntity profile = profileService.getCurrentProfile();

        if(categoryRepository.existsByNameAndProfileId(categoryDTO.getName(), profile.getId())){
            throw new ResponseStatusException(HttpStatus.CONFLICT,"Category with this name already exists");
        }

        CategoryEntity newCategory = toEntity(categoryDTO,profile);
        newCategory = categoryRepository.save(newCategory);
        return toDTO(newCategory);
    }

    public List<CategoryDTO> getCategoriesForCurrentUser(){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByProfileId(profile.getId());
        return categories.stream().map(this::toDTO).toList();
    }

    // get categories by Type for Current User
    public List<CategoryDTO> getCategoriesByTypeForCurrentUser(String type){
        ProfileEntity profile = profileService.getCurrentProfile();
        List<CategoryEntity> categories = categoryRepository.findByTypeAndProfileId(type,profile.getId());
        return categories.stream().map(this::toDTO).toList();

    }

    public CategoryDTO updateCategory(Long categoryId,CategoryDTO dto){
        ProfileEntity profile = profileService.getCurrentProfile();
        CategoryEntity existingCategory = categoryRepository.findByIdAndProfileId(categoryId, profile.getId())
                .orElseThrow(() -> new RuntimeException("Category not found"));

        existingCategory.setName(dto.getName());
        existingCategory.setIcon(dto.getIcon());
        existingCategory = categoryRepository.save(existingCategory);
        return toDTO(existingCategory);

    }

    // Helper Methods
    private CategoryEntity toEntity(CategoryDTO categoryDTO, ProfileEntity profile){
        return CategoryEntity.builder()
                .name(categoryDTO.getName())
                .icon(categoryDTO.getIcon())
                .type(categoryDTO.getType())
                .profile(profile)
                .build();
    }

    private CategoryDTO toDTO(CategoryEntity categoryEntity){
        return CategoryDTO.builder()
                .id(categoryEntity.getId())
                .profileId(categoryEntity.getProfile() != null ? categoryEntity.getProfile().getId() : null)
                .name(categoryEntity.getName())
                .icon(categoryEntity.getIcon())
                .createdAt(categoryEntity.getCreatedAt())
                .updatedAt(categoryEntity.getUpdatedAt())
                .type(categoryEntity.getType())
                .build();
    }

}
