//
//  HomeView.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 04.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import KMPNativeCoroutinesAsync
import shared
import SwiftUI

struct HomeScreen: View {
    @VMStateObject var viewModel: HomeViewModel = KoinSwift.inject()
    @State var state: HomeViewState = .Defaults().Initial

    var body: some View {
        HomeView(state: $state)
            .task {
                do {
                    let sequence = asyncSequence(for: viewModel.stateFlow)
                    for try await state in sequence {
                        self.state = state
                    }
                } catch {
                    print("Failed with error: \(error)")
                }
            }
    }
}

struct HomeView: View {
    @Binding var state: HomeViewState

    var body: some View {
        let configurationState = UiState(state.configuration)
        switch configurationState {
        case .uninitialized:
            Text("Uninitialized")
        case .loading:
            Text("Loading")
        case let .success(configuration):
            Text("Success")
        case let .fail(error):
            Text("Failure: Not setup")
        }
    }
}
