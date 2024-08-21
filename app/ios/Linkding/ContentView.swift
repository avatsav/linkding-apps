import UIKit
import SwiftUI
import LinkdingKt
import OSLog

struct ComposeView: UIViewControllerRepresentable {
    private let component: IosUiComponent
    
    init(component: IosUiComponent) {
        self.component = component
    }
    
    func makeUIViewController(context: Context) -> UIViewController {
        component.uiViewControllerFactory()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    private let component: IosUiComponent
    private let logger: Logger
    
    init(component: IosUiComponent) {
        self.component = component
        self.logger = Logger()
    }
    var body: some View {
        ComposeView(component: component)
                .ignoresSafeArea(.all, edges: .all)
    }
}
