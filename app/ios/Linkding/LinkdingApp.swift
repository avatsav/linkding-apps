import SwiftUI
import LinkdingKt

class AppDelegate: UIResponder, UIApplicationDelegate {
    lazy var applicationComponent: MergedIosAppComponent = createAppComponent(appDelegate: self)
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

private func createAppComponent(appDelegate: AppDelegate) -> MergedIosAppComponent {
    let component = IosAppComponent.companion.createIosAppComponent()
    IosComponentHolder().addComponent(component: component)
    return component
}

private func createUiComponent(appComponent: MergedIosAppComponent) -> IosUiComponent {
    let component = appComponent.createUiComponent()
    IosComponentHolder().addComponent(component: component)
    return component
}
