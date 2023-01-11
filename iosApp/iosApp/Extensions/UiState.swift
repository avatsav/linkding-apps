//
//  UiState.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 05.01.23.
//  Copyright © 2023 orgName. All rights reserved.
//

import Foundation
import shared

public enum UiState<Value, Error> where Value: AnyObject, Error: AnyObject {
    case uninitialized
    case loading(value: Value?)
    case success(value: Value)
    case fail(error: Error)

    public init(_ asyncState: AsyncState<Value, Error>) {
        if asyncState is Uninitialized {
            self = .uninitialized
        } else if let asyncState = asyncState as? shared.Loading<Value> {
            self = .loading(value: asyncState.value)
        } else if let asyncState = asyncState as? shared.Success<Value> {
            self = .success(value: asyncState.value)
        } else if let asyncState = asyncState as? shared.Fail<Error> {
            self = .fail(error: asyncState.error)
        } else {
            fatalError("UiState not in sync with AsyncState")
        }
    }
}
