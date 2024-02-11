import UIKit
import SwiftUI
import shared
import LinkdingKt
import OSLog

struct ComposeView: UIViewControllerRepresentable {
    private let component: IosUiViewControllerComponent
    
    init(component: IosUiViewControllerComponent) {
        self.component = component
    }
    
    func makeUIViewController(context: Context) -> UIViewController {
        component.uiViewControllerFactory()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    private let component: IosUiViewControllerComponent
    private let logger: Logger
    
    init(component: IosUiViewControllerComponent) {
        self.component = component
        self.logger = Logger()
    }
    var body: some View {
        ComposeView(component: component)
                .ignoresSafeArea(.all, edges: .all)
    }
}
