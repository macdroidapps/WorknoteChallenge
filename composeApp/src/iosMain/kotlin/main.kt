import androidx.compose.ui.window.ComposeUIViewController
import ru.macdroid.worknote.App
import ru.macdroid.worknote.di.initKoinIos
import platform.UIKit.UIViewController

private var koinInitialized = false

fun MainViewController(): UIViewController {
    // Инициализируем Koin только один раз
    if (!koinInitialized) {
        initKoinIos()
        koinInitialized = true
    }
    
    return ComposeUIViewController { App() }
}
