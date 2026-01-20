import UIKit
import SwiftUI
import LinkdingKt
import OSLog

struct ComposeView: UIViewControllerRepresentable {
    private let uiGraph: IosUiGraph

    init(uiGraph: IosUiGraph) {
        self.uiGraph = uiGraph
    }

    func makeUIViewController(context: Context) -> UIViewController {
      uiGraph.uiViewControllerFactory.create()
    }

    func updateUIViewController(_ uiViewController: UIViewController, context: Context) {}
}

struct ContentView: View {
    private let uiGraph: IosUiGraph
    private let logger: Logger

    init(uiGraph: IosUiGraph) {
        self.uiGraph = uiGraph
        self.logger = Logger()
    }
    var body: some View {
        ComposeView(uiGraph: uiGraph).ignoresSafeArea(.all, edges: .all)
    }
}
