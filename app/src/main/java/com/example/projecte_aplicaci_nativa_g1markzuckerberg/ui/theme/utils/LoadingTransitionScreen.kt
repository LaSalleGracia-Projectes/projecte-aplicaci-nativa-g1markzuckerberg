import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.projecte_aplicaci_nativa_g1markzuckerberg.ui.theme.utils.FancyLoadingAnimation

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun LoadingTransitionScreen(
    isLoading: Boolean,
    content: @Composable () -> Unit
) {
    AnimatedContent(
        targetState = isLoading,
        transitionSpec = {
            fadeIn(animationSpec = tween(300)) togetherWith fadeOut(animationSpec = tween(300))
        }
    ) { loading ->
        if (loading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                FancyLoadingAnimation(modifier = Modifier.size(200.dp) /* .size(150.dp) por ejemplo si quieres un tamaño específico */)
            }
        } else {
            content()
        }
    }
}
