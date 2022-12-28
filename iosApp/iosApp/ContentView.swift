import shared
import SwiftUI

struct ContentView: View {
    var body: some View {
        let setupPresenter: SetupConfigurationPresenter = KoinSwift.get()
        SetupConfigurationScreen(presenter: setupPresenter)
    }
}
