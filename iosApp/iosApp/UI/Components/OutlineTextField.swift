//
//  OutlineTextField.swift
//  iosApp
//
//  Created by Abhijith Srivatsav on 20.12.22.
//  Copyright © 2022 orgName. All rights reserved.
//

import SwiftUI

struct OutlineTextField: View {
    let title: String
    @Binding var text: String
    let isError: Bool
    let errorText: String

    var body: some View {
        VStack(alignment: .leading, spacing: 16) {
            TextField(title, text: $text)
                .padding()
                .background(
                    RoundedRectangle(cornerRadius: 4.0, style: .continuous)
                        .stroke(.gray, lineWidth: 1.0))
            if isError {
                Text(errorText)
                    .font(.system(size: 14))
                    .foregroundColor(.red)
                    .padding(.leading, 10.0)
                    .padding(.top, 0)
                    .foregroundColor(Color.red)
            }
        }
        .animation(.easeIn(duration: 0.1), value: isError)
    }
}

struct OutlineTextField_Previews: PreviewProvider {
    @State static var text: String = ""

    static var previews: some View {
        OutlineTextField(title: "Username", text: $text, isError: true, errorText: "Error Text")
    }
}
