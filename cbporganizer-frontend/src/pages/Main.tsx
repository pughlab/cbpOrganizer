import React, {useState} from 'react';
import { TabView, TabPanel } from 'primereact/tabview';
import UploadComponent from "../Component/UploadComponent";
import ValidateComponent from "../Component/ValidateComponent";
import ResultComponent from "../Component/ResultComponent";
import SharedDataContext from "../service/SharedDataContext";

const Main = () => {

    const [result, setResult] = useState('');

    return (
        <div className="card">
            <SharedDataContext.Provider value={{ validationResult: result, setValidationResult: setResult }}>
            <TabView>
                <TabPanel header="Upload">
                    <UploadComponent/>
                </TabPanel>
                <TabPanel header="Validate">
                    <ValidateComponent />
                </TabPanel>
                <TabPanel header="Result">
                    <ResultComponent />
                </TabPanel>
            </TabView>
            </SharedDataContext.Provider>
        </div>
    );
}

export default Main;
