import shared
import SwiftUI

struct ContentView: View {
    var body: some View {
        let setupPresenter: SetupPresenter = KoinSwift.get()
        SetupCredentialsScreen(presenter: setupPresenter)
    }
}
