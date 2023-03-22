import {useState} from "react";
import { useNavigate } from "react-router-dom";
import { InputText } from "primereact/inputtext";
import { Button } from 'primereact/button';

const Login = () => {
    const [username, setUsername] = useState<string>('');
    const [password, setPassword] = useState<string>('');

    const navigate = useNavigate();

    const handleLogin = (e: { preventDefault: () => void; }) => {
        // need prevent default, otherwise the page will be reloaded
        e.preventDefault();
        // todo: add login logic
        navigate('/main');
    }

    return (
        <>
            <form onSubmit={handleLogin} className="login-page">
                <p>Login with you Tid</p>
                <div className="flex flex-column gap-2">
                    <label>Username </label>
                    <InputText value={username} onChange={(e) => setUsername(e.target.value)} />

                    <label>Password </label>
                    <InputText value={password} onChange={(e) => setPassword(e.target.value)} />
                </div>
                <br />
                <div>
                    <Button label="Submit" />
                </div>
            </form>
        </>
    );
}

export default Login;
