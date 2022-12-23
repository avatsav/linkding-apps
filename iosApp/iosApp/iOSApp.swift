import shared
import SwiftUI

@main
struct iOSApp: App {
    init() {
        KoinSwift.start()
    }

    var body: some Scene {
        WindowGroup {
            ContentView()
        }
    }
}
