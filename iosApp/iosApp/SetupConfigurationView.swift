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
    @StateObject var viewState: ObservableViewState<SetupConfigurationPresenter.ViewState> = .init(initialState: SetupConfigurationPresenter.ViewStateCompanion().Initial)

    let presenter: SetupConfigurationPresenter

    var body: some View {
        SetupConfigurationView(
            viewState: viewState,
            submitted: { url, apiKey in presenter.setConfiguration(url: url, apiKey: apiKey) }
        )
        .task {
            await viewState.from(asyncStream: asyncStream(for: presenter.uiStateApple))
        }
    }
}

struct SetupConfigurationView: View {
    @ObservedObject var viewState: ObservableViewState<SetupConfigurationPresenter.ViewState>
    var submitted: (String, String) -> Void

    var body: some View {
        NavigationView {
            SetupConfigurationContent(viewState: viewState, submitted: submitted)
                .padding()
                .navigationTitle("Hello there!")
                .navigationBarTitleDisplayMode(.large)
        }
    }
}

struct SetupConfigurationContent: View {
    @ObservedObject var viewState: ObservableViewState<SetupConfigurationPresenter.ViewState>
    var submitted: (String, String) -> Void

    @State private var hostUrl: String = ""
    @State private var apiKey: String = ""

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Enter details and start bookmarking")
            Spacer().frame(height: 16)
            OutlineTextField(title: "Linkding Host URL",
                             text: $hostUrl,
                             isError: viewState.state.error is SetupConfigurationPresenter.ViewStateErrorUrlEmpty,
                             errorText: viewState.state.error.message)
                .disabled(viewState.state.loading)
                .padding(.bottom, 16)
            OutlineTextField(title: "Api Key",
                             text: $apiKey,
                             isError: viewState.state.error is SetupConfigurationPresenter.ViewStateErrorApiKeyEmpty,
                             errorText: viewState.state.error.message)
                .disabled(viewState.state.loading)
            Spacer().frame(height: 16)
            HStack(spacing: 12) {
                Button(action: { submitted(hostUrl, apiKey) }) {
                    Text("Let's go")
                        .font(.headline)
                        .foregroundColor(.white)
                        .padding()
                        .frame(width: 120, height: 40)
                        .background(Color.blue)
                        .cornerRadius(5.0)
                }
                if viewState.state.loading {
                    ProgressView()
                }
                if viewState.state.error is SetupConfigurationPresenter.ViewStateErrorCannotConnect {
                    Text("Cannot connect")
                        .bold()
                        .foregroundColor(Color.red)
                }
            }
            Spacer()
        }
    }
}

struct SetupConfigurationView_Previews: PreviewProvider {
    static var previews: some View {
        SetupConfigurationView(viewState: ObservableViewState<SetupConfigurationPresenter.ViewState>.init(initialState: SetupConfigurationPresenter.ViewStateCompanion().Initial)) { _, _ in
        }
    }
}
