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

struct SetupCredentialsScreen: View {
    @StateObject var viewState: ObservableViewState<SetupPresenter.ViewState> = .init(initialState: SetupPresenter.ViewStateCompanion().Initial)

    let presenter: SetupPresenter

    var body: some View {
        SetupCredentialsView(
            viewState: viewState,
            submitted: { url, apiKey in presenter.setCredentials(url: url, apiKey: apiKey) }
        )
        .task {
            await viewState.from(asyncStream: asyncStream(for: presenter.uiStateApple))
        }
    }
}

struct SetupCredentialsView: View {
    @ObservedObject var viewState: ObservableViewState<SetupPresenter.ViewState>
    var submitted: (String, String) -> Void

    var body: some View {
        NavigationView {
            SetupCredentialsContent(viewState: viewState, submitted: submitted)
                .padding()
                .navigationTitle("Hello there!")
                .navigationBarTitleDisplayMode(.large)
        }
    }
}

struct SetupCredentialsContent: View {
    @ObservedObject var viewState: ObservableViewState<SetupPresenter.ViewState>
    var submitted: (String, String) -> Void

    @State private var hostUrl: String = ""
    @State private var apiKey: String = ""

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Enter details and start bookmarking")
            Spacer().frame(height: 16)
            OutlineTextField(title: "Linkding Host URL",
                             text: $hostUrl,
                             isError: viewState.state.error is SetupPresenter.ViewStateErrorUrlEmpty,
                             errorText: viewState.state.error.message)
                .disabled(viewState.state.loading)
                .padding(.bottom, 16)
            OutlineTextField(title: "Api Key",
                             text: $apiKey,
                             isError: viewState.state.error is SetupPresenter.ViewStateErrorApiKeyEmpty,
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
            }
            Spacer()
        }
    }
}

struct SetupCredentialsView_Previews: PreviewProvider {
    static var previews: some View {
        SetupCredentialsView(viewState: ObservableViewState<SetupPresenter.ViewState>.init(initialState: SetupPresenter.ViewStateCompanion().Initial)) { _, _ in
        }
    }
}
