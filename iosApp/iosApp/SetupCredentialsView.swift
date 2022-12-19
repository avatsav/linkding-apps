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
    @StateObject var viewState: ViewStateObject<SetupPresenter.ViewState> = ViewStateObject<SetupPresenter.ViewState>(initialState: SetupPresenter.ViewStateCompanion().Initial)

    let presenter: SetupPresenter

    init(presenter: SetupPresenter) {
        self.presenter = presenter
    }

    var body: some View {
        NavigationView {
            SetupCredentialsView(viewState: viewState, submitted: { url, apiKey in
                presenter.setCredentials(url: url, apiKey: apiKey)
            })
            .padding()
            .navigationTitle("Hello there!")
            .navigationBarTitleDisplayMode(.large)
        }.onAppear {
            viewState.stateStream = asyncStream(for: presenter.uiStateApple)
            Task {
                await viewState.startObserving()
            }
        }
    }
}

struct SetupCredentialsView: View {
    @ObservedObject var viewState: ViewStateObject<SetupPresenter.ViewState>
    var submitted: (String, String) -> Void

    @State private var hostUrl: String = ""
    @State private var apiKey: String = ""

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            Text("Enter details and start bookmarking")
            Spacer().frame(height: 16)
            TextField("Linkding Host URL", text: $hostUrl)
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 10.0)
                        .strokeBorder(
                            Color.gray,
                            style: StrokeStyle(lineWidth: 1.0)
                        )
                )
                .padding(.bottom, 16)
            TextField("Api Key", text: $apiKey)
                .padding()
                .overlay(
                    RoundedRectangle(cornerRadius: 10.0)
                        .strokeBorder(
                            Color.gray,
                            style: StrokeStyle(lineWidth: 1.0)
                        )
                )
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
            ErrorView(viewState: viewState)
            Spacer()
        }
    }
}

struct ErrorView: View {
    @ObservedObject var viewState: ViewStateObject<SetupPresenter.ViewState>

    var body: some View {
        switch viewState.state.error {
            case is SetupPresenter.ViewStateErrorUrlEmpty:
                Text("Url cannot be empty").foregroundColor(Color.red)
            case is SetupPresenter.ViewStateErrorApiKeyEmpty:
                Text("Api key cannot be empty").foregroundColor(Color.red)
            default:
                EmptyView()
        }
    }
}
