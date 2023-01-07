//
//  HomeView.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 04.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import KMPNativeCoroutinesCombine
import shared
import SwiftUI

//struct HomeScreen: View {
//    @StateObject var viewModel = ObservableViewModel<HomeViewModel>(KoinSwift.inject())
//
//    var body: some View {
//        HomeView(state: viewModel.viewState)
//            .task {
//                // TODO: Find a way to access the methods.
//                // Error: Dynamic key path member lookup cannot refer to instance method 'startObservingViewState()'
//                // Seems the keypaths are for porperties only and can't "mirror" the methods.
//                // viewModel.startObservingViewState()
//            }
//    }
//}

struct HomeScreen2: View {
    let viewModel: HomeViewModel = KoinSwift.inject()

    var body: some View {
        HomeView(state: viewModel.state)
    }
}

struct HomeView: View {
    @State var state: HomeViewState

    init(state: HomeViewState) {
        self.state = state
    }

    var body: some View {
        let configurationState = UiState(state.configuration)
        switch configurationState {
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
