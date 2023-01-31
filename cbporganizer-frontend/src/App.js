import React, {useState} from 'react'
import Keycloak from 'keycloak-js'
import * as ReactDOM from "react-dom";
import axios from "axios";
import {isAuthenticated, logout} from "./service/AuthService";

function App() {
    const [isAuthenticate, setIsAuthenticate] = useState(false);

    function LogOut() {
        const logout = () => {
            // keycloak.logout();
        };
        return (
            <>
                <button onClick={logout}>
                    Logout
                </button>
            </>
        );
    }

    ReactDOM.render(<LogOut/>, document.getElementById("logoutBtn"));

    function CheckRoles() {
        let response = "";
        const check = () => {
            // check if it is authenticated, otherwise redirect to login page
            setIsAuthenticate(isAuthenticated());
            if (!isAuthenticated()) {
                response = "OK";
            } else {
                response = "NOT OK";
                // redirect to login page
            }
        };

        return (
            <>
                <p> Check user has access as cbpuser role</p>
                <button onClick={check}>
                    Check
                </button>
                <p id="cbpsuer"></p>
            </>
        );
    }

    function Login() {

    }

    return (
        <CheckRoles/>
    )
}

export default App
