package com.railabouni.inoventory.openfoodfacts

import com.railabouni.inoventory.product.dto.Product

interface ProductsConnector {
    /** Submits (creates or updates) a product in the OpenFoodFacts database.
     *
     * @param product the product data (EAN, name, brands, etc.)
     * @param images map of image type (e.g. "front", "ingredients", "nutrition") to raw bytes
     * @param userId the UUID of the app user making the contribution (used in the OFF comment field)
     * @param region the OFF region subdomain (e.g. "world", "de", "us") – default "world"
     */
    suspend fun upsertToOpenFoodFacts(
        product: Product,
        images: Map<String, ByteArray>,
        userId: String,
        region: String = "world"
    )
}