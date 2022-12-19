//
//  ViewStateObject.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 19.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation

@MainActor
class ViewStateObject<ViewState>: ObservableObject {
    @Published var state: ViewState
    @Published var error: Error?

    var stateStream: AsyncThrowingStream<ViewState, Error>?

    init(initialState: ViewState) {
        self.state = initialState
    }

    func startObserving() async {
        guard let stateStream = self.stateStream else {
            NSLog("No stream to observe. Did you forget to set the stateStream?")
            return
        }
        do {
            for try await data in stateStream {
                state = data
            }
        } catch {
            NSLog("Failed with error: \(error)")
        }
    }
}
