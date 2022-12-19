//
//  SetupViewModel.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 18.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import shared
import KMPNativeCoroutinesAsync

class SetupViewModel: ObservableObject {
    
    @Published var uiState = SetupPresenter.ViewState(loading: false, error: SetupPresenter.ViewStateErrorNone())
    
    private let presenter: SetupPresenter = SetupPresenter(credentialsStore: CredentialsStore())
    
    func observeViewState() async {
        do {
            let stream = asyncStream(for: presenter.uiStateApple)
            for try await data in stream {
                self.uiState = data
            }
        } catch {
            NSLog("Failed with error: \(error)")
        }
    }
    
    func setCredentials(url:String, apiKey: String) {
        presenter.setCredentials(url: url, apiKey: apiKey)
    }
}
