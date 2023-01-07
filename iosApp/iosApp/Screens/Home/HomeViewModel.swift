//
//  HomeViewModel.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 06.01.23.
//  Copyright © 2023 Abhijith Srivatsav. All rights reserved.
//

import Foundation
import KMPNativeCoroutinesAsync
import shared


// If I extend the class, obviously I can't inject it using the ViewModelsContainer.
// Need to find a way to do this better.
// 1. Delegation over inheritance
// 2. Stop using ViewModelsContainer/Koin and create the instance yourself.

//public class HomeViewModel: shared.HomeViewModel {
//    
//    @Published var viewState: HomeViewState = HomeViewState.companion.Initial
//
//    public func startObservingViewState() async {
//        do {
//            let sequence = asyncSequence(for: stateFlow)
//            for try await state in sequence {
//                print("Got random letters: \(state)")
//                viewState = state
//            }
//        } catch {
//            print("Failed with error: \(error)")
//        }
//    }
//    
//}
