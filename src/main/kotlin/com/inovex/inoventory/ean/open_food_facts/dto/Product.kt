package com.inovex.inoventory.ean.open_food_facts.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
//    val images: Images? = null,
//    val ingredients: List<Ingredient> = emptyList(),
//
//    val languagesCodes: LanguagesCodes? = null,
//
//    val nutrientLevels: NutrientLevels? = null,
//    val nutriments: Nutriments? = null,
//
//    val selectedImages: SelectedImages? = null,
//    val sources: List<Source> = emptyList(),

    val additivesN: Int = 0,

    val additivesOldN: Int = 0,

    val additivesOriginalTags: List<String> = emptyList(),

    val additivesOldTags: List<String> = emptyList(),

    val additivesPrevOriginalTags: List<String> = emptyList(),

    val additivesDebugTags: List<String> = emptyList(),

    val additivesTags: List<String> = emptyList(),
    val allergens: String? = null,

    val allergensFromIngredients: String? = null,

    val allergensFromUser: String? = null,

    val allergensHierarchy: List<String> = emptyList(),

    val allergensLc: String? = null,

    val allergensTags: List<String> = emptyList(),

    val aminoAcidsPrevTags: List<String> = emptyList(),

    val aminoAcidsTags: List<String> = emptyList(),
    val brands: String? = null,

    val brandsDebugTags: List<String> = emptyList(),

    val brandsTags: List<String> = emptyList(),

    val carbonFootprintPercentOfKnownIngredients: String? = null,

    val carbonFootprintFromKnownIngredientsDebug: String? = null,
    val categories: String? = null,

    val categoriesHierarchy: List<String> = emptyList(),

    val categoriesLc: String? = null,

    val categoriesPropertiesTags: List<String> = emptyList(),

    val categoriesTags: List<String> = emptyList(),

    val checkersTags: List<String> = emptyList(),

    val citiesTags: List<String> = emptyList(),
    val code: String? = null,

    val codesTags: List<String> = emptyList(),

    val comparedToCategory: String? = null,
    val complete: Int = 0,

    val completedT: Long = 0,
    val completeness: Double = 0.0,

    val conservationConditions: String? = null,
    val countries: String? = null,

    val countriesHierarchy: List<String> = emptyList(),

    val countriesLc: String? = null,

    val countriesDebugTags: List<String> = emptyList(),

    val countriesTags: List<String> = emptyList(),

    val correctorsTags: List<String> = emptyList(),

    val createdT: Long = 0,
    val creator: String? = null,

    val dataQualityBugsTags: List<String> = emptyList(),

    val dataQualityErrorsTags: List<String> = emptyList(),

    val dataQualityInfoTags: List<String> = emptyList(),

    val dataQualityTags: List<String> = emptyList(),

    val dataQualityWarningsTags: List<String> = emptyList(),

    val dataSources: String? = null,

    val dataSourcesTags: List<String> = emptyList(),

    val debugParamSortedLangs: List<String> = emptyList(),

    val editorsTags: List<String> = emptyList(),

    val embCodes: String? = null,

    val embCodesDebugTags: List<String> = emptyList(),

    val embCodesOrig: String? = null,

    val embCodesTags: List<String> = emptyList(),

    val entryDatesTags: List<String> = emptyList(),

    val expirationDate: String? = null,

    val expirationDateDebugTags: List<String> = emptyList(),

    val fruitsVegetablesNuts100GEstimate: Int = 0,

    val genericName: String? = null,
    val id: String? = null,
    val _id: String? = null,

    val imageFrontSmallUrl: String? = null,

    val imageFrontThumbUrl: String? = null,

    val imageFrontUrl: String? = null,

    val imageIngredientsUrl: String? = null,

    val imageIngredientsSmallUrl: String? = null,

    val imageIngredientsThumbUrl: String? = null,

    val imageNutritionSmallUrl: String? = null,

    val imageNutritionThumbUrl: String? = null,

    val imageNutritionUrl: String? = null,

    val imageSmallUrl: String? = null,

    val imageThumbUrl: String? = null,

    val imageUrl: String? = null,

    val informersTags: List<String> = emptyList(),

    val ingredientsAnalysisTags: List<String> = emptyList(),

    val ingredientsDebug: List<String?> = emptyList(),

    val ingredientsFromOrThatMayBeFromPalmOilN: Int = 0,

    val ingredientsFromPalmOilTags: List<String> = emptyList(),

    val ingredientsFromPalmOilN: Int = 0,

    val ingredientsHierarchy: List<String> = emptyList(),

    val ingredientsIdsDebug: List<String> = emptyList(),

    val ingredientsN: Int = 0,

    val ingredientsNTags: List<String> = emptyList(),

    val ingredientsOriginalTags: List<String> = emptyList(),

    val ingredientsTags: List<String> = emptyList(),

    val ingredientsText: String? = null,

    val ingredientsTextDebug: String? = null,

    val ingredientsTextWithAllergens: String? = null,

    val ingredientsThatMayBeFromPalmOilN: Int = 0,

    val ingredientsThatMayBeFromPalmOilTags: List<String> = emptyList(),

    val interfaceVersionCreated: String? = null,

    val interfaceVersionModified: String? = null,

    val keywords: List<String> = emptyList(),

    val knownIngredientsN: Int = 0,
    val labels: String? = null,

    val labelsHierarchy: List<String> = emptyList(),

    val labelsLc: String? = null,

    val labelsPrevHierarchy: List<String> = emptyList(),

    val labelsPrevTags: List<String> = emptyList(),

    val labelsTags: List<String> = emptyList(),

    val labelsDebugTags: List<String> = emptyList(),
    val lang: String? = null,

    val langDebugTags: List<String> = emptyList(),

    val languagesHierarchy: List<String> = emptyList(),

    val languagesTags: List<String> = emptyList(),

    val lastEditDatesTags: List<String> = emptyList(),

    val lastEditor: String? = null,

    val lastImageDatesTags: List<String> = emptyList(),

    val lastImageT: Long = 0,

    val lastModifiedBy: String? = null,

    val lastModifiedT: Long = 0,
    val lc: String? = null,
    val link: String? = null,

    val linkDebugTags: List<String> = emptyList(),

    val manufacturingPlaces: String? = null,

    val manufacturingPlacesDebugTags: List<String> = emptyList(),

    val manufacturingPlacesTags: List<String> = emptyList(),

    val maxImgid: String? = null,

    val mineralsPrevTags: List<String> = emptyList(),

    val mineralsTags: List<String> = emptyList(),

    val miscTags: List<String> = emptyList(),

    val netWeightUnit: String? = null,

    val netWeightValue: String? = null,

    val nutritionDataPer: String? = null,

    val nutritionScoreWarningNoFruitsVegetablesNuts: Int = 0,

    val noNutritionData: String? = null,

    val novaGroup: String? = null,

    val novaGroups: String? = null,

    val novaGroupDebug: String? = null,

    val novaGroupTags: List<String> = emptyList(),

    val novaGroupsTags: List<String> = emptyList(),

    val nucleotidesPrevTags: List<String> = emptyList(),

    val nucleotidesTags: List<String> = emptyList(),

    val nutrientLevelsTags: List<String> = emptyList(),

    val nutritionData: String? = null,

    val nutritionDataPerDebugTags: List<String> = emptyList(),

    val nutritionDataPrepared: String? = null,

    val nutritionDataPreparedPer: String? = null,

    val nutritionGrades: String? = null,

    val nutritionScoreBeverage: Int = 0,

    val nutritionScoreDebug: String? = null,

    val nutritionScoreWarningNoFiber: Int = 0,

    val nutritionGradesTags: List<String> = emptyList(),
    val origins: String? = null,

    val originsDebugTags: List<String> = emptyList(),

    val originsTags: List<String> = emptyList(),

    val otherInformation: String? = null,

    val otherNutritionalSubstancesTags: List<String> = emptyList(),
    val packaging: String? = null,

    val packagingDebugTags: List<String> = emptyList(),

    val packagingTags: List<String> = emptyList(),

    val photographersTags: List<String> = emptyList(),

    val pnnsGroups1: String? = null,

    val pnnsGroups2: String? = null,

    val pnnsGroups1Tags: List<String> = emptyList(),

    val pnnsGroups2Tags: List<String> = emptyList(),

    val popularityKey: Long = 0,

    val producerVersionId: String? = null,

    @SerialName("product_name")
    val productName: String? = null,

    val productQuantity: String? = null,

    val purchasePlaces: String? = null,

    val purchasePlacesDebugTags: List<String> = emptyList(),

    val purchasePlacesTags: List<String> = emptyList(),

    val qualityTags: List<String> = emptyList(),
    val quantity: String? = null,

    val quantityDebugTags: List<String> = emptyList(),

    val recyclingInstructionsToDiscard: String? = null,
    val rev: Int = 0,

    val servingQuantity: String? = null,

    val servingSize: String? = null,

    val servingSizeDebugTags: List<String> = emptyList(),
    val sortkey: Long = 0,
    val states: String? = null,

    val statesHierarchy: List<String> = emptyList(),

    val statesTags: List<String> = emptyList(),
    val stores: String? = null,

    val storesDebugTags: List<String> = emptyList(),

    val storesTags: List<String> = emptyList(),
    val traces: String? = null,

    val tracesFromIngredients: String? = null,

    val tracesHierarchy: List<String> = emptyList(),

    val tracesDebugTags: List<String> = emptyList(),

    val tracesFromUser: String? = null,

    val tracesLc: String? = null,

    val tracesTags: List<String> = emptyList(),

    val unknownIngredientsN: Int = 0,

    val unknownNutrientsTags: List<String> = emptyList(),

    val updateKey: String? = null,

    val vitaminsPrevTags: List<String> = emptyList(),

    val vitaminsTags: List<String> = emptyList(),
)