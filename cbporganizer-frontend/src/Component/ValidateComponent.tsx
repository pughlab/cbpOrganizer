import React, {useContext, useEffect, useState} from 'react'
import useAxios from "../service/useAxios";
import {ProgressSpinner} from "primereact/progressspinner";
import SharedDataContext from "../service/SharedDataContext";


const ValidateComponent = () => {

    const { response, loading, error, operation } = useAxios();
    const { response: responseReport, loading: loadingReport, error: errorReport, operation: operationReport } = useAxios();
    const { response: responseURL, loading: loadingURL, error: errorURL, operation: operationURL } = useAxios();

    // validation message
    const [validateMessage, setValidateMessage] = useState('');
    // show the html result button
    const [validateResponse, setValidateResponse] = useState<any>();

    // test
    const [htmlBlob, setHtmlBlob] = useState("");
    const { validationResult, setValidationResult } = useContext(SharedDataContext);

    useEffect(() => {
        console.log('response: ', response);
        if (response !== null) {
            setValidateMessage("Validation finished");
            setValidateResponse(response);
        }
    }, [response]);

    useEffect(() => {
        console.log('responseReport: ', responseReport);
        if (responseReport !== null) {
            const file = new Blob([validateResponse], {type: 'text/html'});
            const blobURL = URL.createObjectURL(file);
            setHtmlBlob(blobURL);
            setValidationResult(blobURL);
        }
    }, [responseReport]);

    useEffect(() => {
        if (responseURL !== null) {
            validateResponse(responseURL);
        }
    }, [responseURL]);

    // check if there's any error
    useEffect(() => {
        if (error !== null) {
            setValidateMessage(error)
        }
    }, [error]);

    const handleValidate = () => {
        operation({
            method: 'GET',
            url: 'validation-report-blob',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            },
        })
    }

    const handleShowURL = () => {
        const file = new Blob([validateResponse], {type: 'text/html'});
        const fileURL = URL.createObjectURL(file);
        window.open(fileURL);
    }

    const handleShowReport = () => {
        operationReport({
            method: 'GET',
            url: 'validation-report-html',
            headers: {
                'Content-type': 'application/json; charset=UTF-8',
            },
        })
    }

    return (
      <>
          <p>Validate your files with the dataset validator </p>
          <button onClick={handleValidate}>validate</button>
          <button onClick={handleShowURL}>show report new window</button>
          <button onClick={handleShowReport}>show report inline</button>
          {loading && <div className="card flex justify-content-center">
              <ProgressSpinner />
          </div>}
          <br/>
          <br/>
          {validateMessage && <p>{validateMessage}</p>}
          <br />
          {validateResponse && (
              <div>
                  <button onClick={handleShowURL}>Open Result</button>
              </div>
          )}
          {htmlBlob && (
              <iframe src={htmlBlob} title="HTML Content" width="100%"
                      height="70%" />
          )}
      </>
    );
}

export default ValidateComponent
