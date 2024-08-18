import SwiftUI
import LinkdingKt

class AppDelegate: UIResponder, UIApplicationDelegate {
    lazy var applicationComponent: IosAppComponent = createAppComponent(appDelegate: self)
}

@main
struct LinkdingApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    var body: some Scene {
        WindowGroup {
            let uiComponent = createUiComponent(
                appComponent: delegate.applicationComponent
            )
            ContentView(component: uiComponent)
        }
    }
    
    private func createUiComponent(appComponent: IosAppComponent) -> IosUiViewControllerComponent {
        var uiComponent: IosUiViewControllerComponent? = nil
        uiComponent = IosUiViewControllerComponent.companion.create(appComponent:appComponent) { ModelsApiConfig in
            // Now we can use 'uiComponent' inside the closure
            createUserComponent(apiConfig: ModelsApiConfig, uiComponent: uiComponent!)
        }
        return uiComponent!
    }
    
    private func createUserComponent(apiConfig: ModelsApiConfig, uiComponent: IosUiViewControllerComponent) -> IosUserComponent {
        return IosUserComponent.companion.create(apiConfig: apiConfig, uiComponent: uiComponent)
    }
}

private func createAppComponent(appDelegate: AppDelegate) -> IosAppComponent {
    return IosAppComponent.companion.create()
}

