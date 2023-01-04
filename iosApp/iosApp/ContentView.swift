import shared
import SwiftUI

struct ContentView: View {
    var body: some View {
        let setupPresenter: HomePresenter = KoinSwift.get()
        SetupConfigurationScreen(presenter: setupPresenter)
    }
}
