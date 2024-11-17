import SwiftUI
import LinkdingKt

class AppDelegate: UIResponder, UIApplicationDelegate {
    lazy var applicationComponent: IosAppComponent = createAppComponent(appDelegate: self)
    
    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        applicationComponent.appInitializer.initialize()
        return true
    }
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
    let component = IosComponentCreator().createAppComponent()
    IosComponentHolder().addComponent(component: component)
    return component
}

private func createUiComponent(appComponent: IosAppComponent) -> IosUiComponent {
    let component = IosComponentCreator().createUiComponent(appComponent: appComponent)
    IosComponentHolder().addComponent(component: component)
    return component
}
