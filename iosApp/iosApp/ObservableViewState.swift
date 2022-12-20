//
//  ViewStateObject.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 19.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import Foundation
import SwiftUI

@MainActor
class ObservableViewState<ViewState>: ObservableObject {
    
    @Published var state: ViewState

    init(initialState: ViewState) {
        self.state = initialState
    }

    func from(asyncStream: AsyncThrowingStream<ViewState, Error>) async {
        do {
            for try await data in asyncStream {
                state = data
            }
        } catch {
            NSLog("Failed with error: \(error)")
        }
    }
}
