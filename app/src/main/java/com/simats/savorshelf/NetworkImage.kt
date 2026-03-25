package com.simats.savorshelf

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.DefaultAlpha
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.Alignment

private val urlToDrawableMap = mapOf(
    "https://image2url.com/r2/default/images/1771904762259-2bcbdf5f-f219-436b-814c-94e901bb5db0.png" to R.drawable.splash_screen,
    "https://image2url.com/r2/default/images/1771904985641-1c84a847-8323-4552-8b12-4a632560de8b.png" to R.drawable.activity_1,
    "https://image2url.com/r2/default/images/1771905170050-6bee2399-1892-486e-a47c-9335e022efeb.png" to R.drawable.activity_2,
    "https://image2url.com/r2/default/images/1772077653265-7ca43531-9f4f-4426-808b-ae94051f9215.png" to R.drawable.ex_front,
    "https://image2url.com/r2/default/images/1772077769506-62b5a25c-49eb-4298-b492-05f58ae71201.png" to R.drawable.ex_expiry,
    "https://image2url.com/r2/default/images/1772089602406-d79a74d4-0933-4015-8e86-8f96b87ec3cc.png" to R.drawable.get_started,
    "https://image2url.com/r2/default/images/1772091005337-5389eaca-6ec9-425b-b4b3-b15ce30751bb.png" to R.drawable.labeled_choice,
    "https://image2url.com/r2/default/images/1772091245372-fc42fb44-c713-469e-8368-7c12b981b14a.png" to R.drawable.unlabeled_choice,
    "https://image2url.com/r2/default/images/1772164873232-c166ef95-c445-4f7d-9852-4f48d008db1c.png" to R.drawable.app_logo,
    "https://image2url.com/r2/default/images/1772165942529-0db3e05f-ebef-48b7-9133-fc8662d9a661.png" to R.drawable.background,
    "https://images.pexels.com/photos/2733918/pexels-photo-2733918.jpeg?auto=compress&cs=tinysrgb&w=400&h=400&fit=crop" to R.drawable.leafy_greens,
    "https://images.unsplash.com/photo-1550583724-b2692b85b150?w=200&h=200&fit=crop" to R.drawable.dairy,
    "https://images.unsplash.com/photo-1557844352-761f2565b576?w=400&h=400&fit=crop" to R.drawable.vegetables,
    "https://images.unsplash.com/photo-1596040033229-a9821ebd058d?w=400&h=400&fit=crop" to R.drawable.herbs_seasonings,
    "https://images.unsplash.com/photo-1607623814075-e51df1bdc82f?w=400&h=400&fit=crop" to R.drawable.meat_seafood,
    "https://images.unsplash.com/photo-1610832958506-aa56368176cf?w=400&h=400&fit=crop" to R.drawable.fruits,
)

fun getOfflineFallback(url: String): Int? {
    return urlToDrawableMap[url]
}

@Composable
fun NetworkImage(
    model: Any?,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Fit,
    colorFilter: ColorFilter? = null,
    alpha: Float = DefaultAlpha,
    alignment: Alignment = Alignment.Center
) {
    val placeholderId = (model as? String)?.let { getOfflineFallback(it) }
    val placeholderPainter = placeholderId?.let { painterResource(id = it) }
    
    coil.compose.AsyncImage(
        model = model,
        contentDescription = contentDescription,
        modifier = modifier,
        contentScale = contentScale,
        colorFilter = colorFilter,
        alpha = alpha,
        alignment = alignment,
        placeholder = placeholderPainter,
        error = placeholderPainter
    )
}
