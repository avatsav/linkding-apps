//
//  HomeView.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 04.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import shared
import SwiftUI

struct HomeScreen: View {
    let homeViewModel: HomeViewModel = KoinSwift.inject()

    var body: some View {
        let viewModel = ObservableViewModel(homeViewModel)
        let state = viewModel.state
        HomeView(state: state)
    }
}

struct HomeView: View {
    let state: HomeViewState
    var setupUiState: UiState<Configuration, HomeViewState.NotSetup>

    init(state: HomeViewState) {
        self.state = state
        self.setupUiState = UiState(state.configuration)
    }

    var body: some View {
        switch setupUiState {
        case .uninitialized:
            Text("Uninitialized")
        case .loading:
            Text("Loading")
        case .success:
            Text("Success")
        case .fail:
            Text("Failure")
        }
    }
}
