import SwiftUI
import LinkdingKt

class AppDelegate: UIResponder, UIApplicationDelegate {
    lazy var appGraph: IosAppGraph = createAppGraph(appDelegate: self)

    func application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey : Any]? = nil) -> Bool {
        appGraph.appInitializer.initialize()
        return true
    }
}

@main
struct LinkdingApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    var body: some Scene {
        WindowGroup {
            let uiGraph = createUiGraph(appGraph: delegate.appGraph)
            ContentView(uiGraph: uiGraph)
        }
    }
}

private func createAppGraph(appDelegate: AppDelegate) -> IosAppGraph {
    let appGraph = IosGraphCreator.shared.createAppGraph()
    IosGraphHolder.shared.addGraph(graph: appGraph)
    return appGraph
}

private func createUiGraph(appGraph: IosAppGraph) -> IosUiGraph {
    let uiGraph = IosGraphCreator.shared.createUiGraph(appGraph: appGraph)
    IosGraphHolder.shared.addGraph(graph: uiGraph)
    return uiGraph
}
