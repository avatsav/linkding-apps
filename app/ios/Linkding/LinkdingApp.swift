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
}

private func createAppComponent(appDelegate: AppDelegate) -> IosAppComponent {
    return IosAppComponent.companion.create()
}

private func createUiComponent(appComponent: IosAppComponent) -> IosUiViewControllerComponent {
    return IosUiViewControllerComponent.companion.create(appComponent: appComponent)
}
