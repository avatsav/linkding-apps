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
    let homePresenter: HomePresenter = KoinSwift.get()

    var body: some View {
        var uiState = homePresenter.uiState
        HomeView(state: uiState)
    }
}

struct HomeView: View {
    let state: HomeViewState
    var setupUiState: UiState<KotlinBoolean, NSString>

    init(state: HomeViewState) {
        self.state = state
        self.setupUiState = UiState(state.setupState)
    }

    var body: some View {
        switch setupUiState {
        case .uninitialized:
            Text("Unitialized")
        case .loading(_):
            Text("Loading")
        case .success(_):
            Text("Success")
        case .fail(_):
            Text("Failure")
        }
    }
}
