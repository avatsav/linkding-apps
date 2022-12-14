//
//  SetupCredentialsView.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 17.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import KMPNativeCoroutinesAsync
import shared
import SwiftUI

struct SetupConfigurationScreen: View {
//    @StateObject var viewState: ObservableViewState<HomeViewState> = .init(initialState: HomeViewState.companion.Initial)

    let viewModel: HomeViewModel

    var body: some View {
        Text("Hello World")
//        SetupConfigurationView(
//            viewState: viewState,
//            submitted: { url, apiKey in presenter.setConfiguration(url: url, apiKey: apiKey) }
//        ).task {
//            await viewState.from(asyncSequence: asyncSequence(for: presenter.uiStateFlow))
//        }
    }
}

// struct SetupConfigurationView: View {
//    @ObservedObject var viewState: ObservableViewState<HomeViewState>
//    var submitted: (String, String) -> Void
//
//    var body: some View {
//        NavigationView {
//            SetupConfigurationContent(viewState: viewState, submitted: submitted)
//                .padding()
//                .navigationTitle("Setup Linkding")
//                .navigationBarTitleDisplayMode(.large)
//        }
//    }
// }
//
// struct SetupConfigurationContent: View {
//    @ObservedObject var viewState: ObservableViewState<HomeViewState>
//    var submitted: (String, String) -> Void
//
//    @State private var hostUrl: String = ""
//    @State private var apiKey: String = ""
//
//    var body: some View {
//        VStack(alignment: .leading, spacing: 16) {
//            Text("Configure settings, so that the app can communicate with your linkding installation.")
//            Spacer().frame(height: 16)
//            OutlineTextField(title: "Linkding Host URL",
//                             text: $hostUrl,
//                             isError: viewState.state.error is HomeViewState,
//                             errorText: viewState.state.error.message)
//                .disabled(viewState.state.loading)
//                .padding(.bottom, 16)
//            OutlineTextField(title: "Api Key",
//                             text: $apiKey,
//                             isError: viewState.state.error is HomeViewState,
//                             errorText: viewState.state.error.message)
//                .disabled(viewState.state.loading)
//            Spacer().frame(height: 16)
//            HStack(spacing: 12) {
//                Button(action: { submitted(hostUrl, apiKey) }) {
//                    Text("Let's go")
//                        .font(.headline)
//                        .foregroundColor(.white)
//                        .padding()
//                        .frame(width: 120, height: 40)
//                        .background(Color.blue)
//                        .cornerRadius(5.0)
//                }
//                if viewState.state.loading {
//                    ProgressView()
//                }
//                if viewState.state.error is HomeViewState {
//                    Text("Cannot connect")
//                        .bold()
//                        .foregroundColor(Color.red)
//                }
//            }
//            Spacer()
//        }
//    }
// }

// struct SetupConfigurationView_Previews: PreviewProvider {
//    static var previews: some View {
//        SetupConfigurationView(viewState: ObservableViewState<SetupConfigurationPresenter.ViewState>.init(initialState: SetupConfigurationPresenter.ViewStateCompanion().Initial)) { _, _ in
//        }
//    }
// }
