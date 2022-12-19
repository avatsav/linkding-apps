import SwiftUI
import shared

struct ContentView: View {
    var body: some View {
        SetupCredentialsScreen(presenter: SetupPresenter(credentialsStore: CredentialsStore()))
    }
}

struct ContentView_Previews: PreviewProvider {
    static var previews: some View {
        ContentView()
    }
}
