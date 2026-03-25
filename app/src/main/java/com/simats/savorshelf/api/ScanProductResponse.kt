package com.simats.savorshelf.api

data class ScanProductResponse(
    val status: String,
    val detected_text: String?,
    val extracted_data: ExtractedData?
)

data class ExtractedData(
    val expiry_date: String?,
    val mfg_date: String?,
    val lot_number: String?
)
