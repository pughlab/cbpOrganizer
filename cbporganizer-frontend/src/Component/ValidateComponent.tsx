import React, {useContext, useEffect, useState} from 'react'
import useAxios from "../service/useAxios";
import {ProgressSpinner} from "primereact/progressspinner";
import SharedDataContext from "../service/SharedDataContext";


const ValidateComponent = () => {

    const { response, loading, error, operation } = useAxios();
    const { response: responseReport, loading: loadingReport, error: errorReport, operation: operationReport } = useAxios();

    // validation message
    const [validateMessage, setValidateMessage] = useState('');

    // setting the shared context for the result component
    const { validationResult, setValidationResult } = useContext(SharedDataContext);

    useEffect(() => {
        if (response !== null) {
            setValidateMessage("Validation finished");
            retreieveReportAsBlob();
        }
    }, [response]);

    useEffect(() => {
        if (responseReport !== null) {
            const file = new Blob([responseReport], {type: 'text/html'});
            const blobURL = URL.createObjectURL(file);
            setValidationResult(blobURL);
        }
    }, [responseReport]);

    // check if there's any error
    useEffect(() => {
        if (error !== null) {
            setValidateMessage(error)
        }
    }, [error]);

    const handleValidate = () => {
        operation({
            method: 'GET',
            url: 'validate',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            },
        })
    }

    const retreieveReportAsBlob = () => {
        operationReport({
            method: 'GET',
            url: 'validation-report-blob',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            },
        })
    }

    return (
      <>
          <p>Validate your files with the dataset validator </p>
          <button onClick={handleValidate}>validate</button>
          {loading && <div className="card flex justify-content-center">
              <ProgressSpinner />
          </div>}
          <br/>
          <br/>
          {validateMessage && <p>{validateMessage}</p>}
      </>
    );
}

export default ValidateComponent
