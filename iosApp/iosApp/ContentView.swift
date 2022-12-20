import shared
import SwiftUI

struct ContentView: View {
    var body: some View {
        let setupPresenter = SetupPresenter(credentialsStore: CredentialsStore())
        SetupCredentialsScreen(presenter: setupPresenter)
    }
}
